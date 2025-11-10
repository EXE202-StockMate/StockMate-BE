package com.stock_mate.BE.service;

import jakarta.persistence.criteria.JoinType;
import com.stock_mate.BE.dto.request.BOM.BOMCreateRequest;
import com.stock_mate.BE.dto.request.BOM.BOMItemRequest;
import com.stock_mate.BE.dto.request.BOM.BOMUpdateRequest;
import com.stock_mate.BE.dto.response.BOM.*;
import com.stock_mate.BE.entity.*;
import com.stock_mate.BE.enums.MaterialType;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.BOMMapper;
import com.stock_mate.BE.repository.*;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BOMService extends BaseSpecificationService<BOMHeader, BOMResponse> {

    @Autowired
    private BOMHeaderRepository bomRepository;
    @Autowired
    private BOMItemRepository bomItemRepository;
    @Autowired
    private FinishProductRepository finishProductRepository;
    @Autowired
    private RawMaterialRepository rawMaterialRepository;
    @Autowired
    private SemiFinishProductRepository semiFinishProductRepository;
    @Autowired
    private BOMMapper bomMapper;

    @Transactional
    public BOMResponse createBOM(BOMCreateRequest request) {
        // Kiểm tra finish product tồn tại
        FinishProduct finishProduct = finishProductRepository.findById(request.finishProductId())
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND,
                        "Không tìm thấy sản phẩm với ID: " + request.finishProductId()));

        // Kiểm tra BOM đã tồn tại chưa
        if (bomRepository.findByFinishProduct_FgID(request.finishProductId()).isPresent()) {
            throw new AppException(ErrorCode.BOM_ALREADY_EXISTS,
                    "BOM đã tồn tại cho sản phẩm này");
        }

        // Tạo BOM Header
        BOMHeader bomHeader = new BOMHeader();
        bomHeader.setFinishProduct(finishProduct);

        BOMHeader savedBOM = bomRepository.save(bomHeader);

        // Tạo BOM Items
        List<BOMItem> bomItems = request.items().stream()
                .map(itemReq -> createBOMItem(itemReq, savedBOM))
                .collect(Collectors.toList());

        bomItemRepository.saveAll(bomItems);
        savedBOM.setItems(bomItems);

        return bomMapper.toDto(savedBOM);
    }
    @Transactional
    public BOMResponse updateBOM(int bomId, BOMUpdateRequest request) {
        BOMHeader bomHeader = bomRepository.findById(bomId)
                .orElseThrow(() -> new AppException(ErrorCode.BOM_NOT_FOUND,
                        "Không tìm thấy BOM với ID: " + bomId));

        // Xóa tung item ra khoi collection hien tai
        bomHeader.getItems().clear();

        //Flush de Hibernate xoa cac item cu trong db
        bomItemRepository.flush();

        // Tạo items mới và thêm vào collection hiện tại
        List<BOMItem> newItems = request.items().stream()
                .map(itemReq -> createBOMItem(itemReq, bomHeader))
                .collect(Collectors.toList());

        bomHeader.getItems().addAll(newItems);

        BOMHeader updated = bomRepository.save(bomHeader);
        return bomMapper.toDto(updated);
    }

    public BOMResponse getBOMById(int bomId) {
        BOMHeader bomHeader = bomRepository.findById(bomId)
                .orElseThrow(() -> new AppException(ErrorCode.BOM_NOT_FOUND,
                        "Không tìm thấy BOM với ID: " + bomId));
        return bomMapper.toDto(bomHeader);
    }

    public Map<String, Integer> calculateMaterialRequirement(String productId, int orderQuantity){
        Optional<BOMHeader> bomOpt = bomRepository.findByFinishProduct_FgID(productId);
        if (bomOpt.isEmpty()) {
            throw new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND, "BOM không tồn tại cho sản phẩm hoàn thiện: " + productId);
        }

        BOMHeader bom = bomOpt.get();
        Map<String, Integer> requirements = new HashMap<>();

        for (BOMItem item : bom.getItems()) {
            String materialId = item.getMaterialType() == com.stock_mate.BE.enums.MaterialType.RAW_MATERIAL ?
                    item.getRawMaterial().getRmID() : item.getSemiFinishProduct().getSfgID();

            int totalRequired = item.getQuantity() * orderQuantity;
            requirements.put(materialId, totalRequired);
        }

        return requirements;
    }

    private BOMItem createBOMItem(BOMItemRequest request, BOMHeader bomHeader) {
        BOMItem item = new BOMItem();
        item.setBomHeader(bomHeader);
        item.setMaterialType(request.materialType());
        item.setQuantity(request.quantity());
        item.setUnit(request.unit());

        if (request.materialType() == MaterialType.RAW_MATERIAL) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(request.materialId())
                    .orElseThrow(() -> new AppException(ErrorCode.RAW_MATERIAL_NOT_FOUND,
                            "Không tìm thấy nguyên liệu: " + request.materialId()));
            item.setRawMaterial(rawMaterial);
        } else {
            SemiFinishProduct sfp = semiFinishProductRepository.findById(request.materialId())
                    .orElseThrow(() -> new AppException(ErrorCode.SEMI_FINISH_PRODUCT_NOT_FOUND,
                            "Không tìm thấy bán thành phẩm: " + request.materialId()));
            item.setSemiFinishProduct(sfp);
        }

        return item;
    }

    @Override
    protected JpaSpecificationExecutor<BOMHeader> getRepository() {
        return bomRepository;
    }

    @Override
    protected Function<BOMHeader, BOMResponse> getMapper() {
        return bomMapper::toDto;
    }

    @Override
    protected Specification<BOMHeader> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";

            // Join với items để truy cập rawMaterial và semiFinishProduct
            var itemsJoin = root.join("items", JoinType.LEFT);
            var rawMaterialJoin = itemsJoin.join("rawMaterial", JoinType.LEFT);
            var semiFinishJoin = itemsJoin.join("semiFinishProduct", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("finishProduct").get("fgID")), searchPattern),
                    cb.like(cb.lower(root.get("finishProduct").get("name")), searchPattern),
                    cb.like(cb.lower(rawMaterialJoin.get("rmID")), searchPattern),
                    cb.like(cb.lower(rawMaterialJoin.get("name")), searchPattern),
                    cb.like(cb.lower(semiFinishJoin.get("sfgID")), searchPattern),
                    cb.like(cb.lower(semiFinishJoin.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("note")), searchPattern)
            );
        };
    }

    public void deleteBOM(int bomId) {
        BOMHeader bomHeader = bomRepository.findById(bomId)
                .orElseThrow(() -> new AppException(ErrorCode.BOM_NOT_FOUND,
                        "Không tìm thấy BOM với ID: " + bomId));
        bomRepository.delete(bomHeader);
    }
}
