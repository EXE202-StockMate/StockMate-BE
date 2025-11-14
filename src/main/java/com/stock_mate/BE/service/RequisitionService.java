package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.RequisitionRequest;
import com.stock_mate.BE.dto.response.RequisitionResponse;
import com.stock_mate.BE.entity.Requisition;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.FinishProductMapper;
import com.stock_mate.BE.mapper.RawMaterialMapper;
import com.stock_mate.BE.mapper.RequisitionMapper;
import com.stock_mate.BE.mapper.SemiFinishProductMapper;
import com.stock_mate.BE.repository.FinishProductRepository;
import com.stock_mate.BE.repository.RawMaterialRepository;
import com.stock_mate.BE.repository.RequisitionRepository;
import com.stock_mate.BE.repository.SemiFinishProductRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequisitionService extends BaseSpecificationService<Requisition, RequisitionResponse> {

    @Autowired
    RequisitionRepository requisitionRepository;
    @Autowired
    RequisitionMapper requisitionMapper;
    @Autowired
    RawMaterialRepository rawMaterialRepository;
    @Autowired
    RawMaterialMapper rawMaterialMapper;
    @Autowired
    SemiFinishProductMapper semiFinishProductMapper;
    @Autowired
    SemiFinishProductRepository semiFinishProductRepository;
    @Autowired
    FinishProductRepository finishProductRepository;
    @Autowired
    FinishProductMapper finishProductMapper;

    @Override
    protected JpaSpecificationExecutor<Requisition> getRepository() {
        return requisitionRepository;
    }

    @Override
    protected Function<Requisition, RequisitionResponse> getMapper() {
        return requisitionMapper::toDto;
    }


    @Override
    protected Specification<Requisition> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("type")), searchPattern),
                    cb.like(cb.lower(root.get("unit")), searchPattern),
                    cb.like(cb.lower(root.get("note")), searchPattern)
            );
        };
    }

    public RequisitionResponse getById(String id) {
        Requisition r = requisitionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REQUISITION_NOT_FOUND));
        return mapProductMaterial(r);
    }

    @Transactional
    public RequisitionResponse createRequisition(RequisitionRequest request) {

        Requisition r = new Requisition();

        // luu id vat lieu/san pham vao materialID
        r.setMaterialID(request.materialID());
        r.setType(request.type());

        if(request.quantity() <= 0){
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Số lượng phải lớn hơn 0");
        }

        r.setQuantity(request.quantity());
        r.setUnit(request.unit());
        r.setNote(request.note());
        r.setCreateDate(LocalDate.now());
        r.setUpdateDate(LocalDate.now());
        requisitionRepository.save(r);

        //map de response ve co chua rawMaterial/SemiFinishProduct/FinishProduct
        return mapProductMaterial(r);
    }

    @Transactional
    public RequisitionResponse updateRequisition(String id, RequisitionRequest request) {
        Requisition r = requisitionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.REQUISITION_NOT_FOUND, "Yêu cầu vật tư không tồn tại")
        );
        r.setMaterialID(request.materialID());
        r.setType(request.type());
        if(request.quantity() <= 0){
            throw new AppException(ErrorCode.INVALID_QUANTITY, "Số lượng phải lớn hơn 0");
        }
        r.setQuantity(request.quantity());
        r.setUnit(request.unit());
        r.setNote(request.note());
        r.setUpdateDate(LocalDate.now());
        requisitionRepository.save(r);
        return mapProductMaterial(r);
    }

    @Transactional
    public void deleteRequisition(String id) {
        Requisition r = requisitionRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.REQUISITION_NOT_FOUND, "Yêu cầu vật tư không tồn tại")
        );
        requisitionRepository.delete(r);
    }

    private RequisitionResponse mapProductMaterial(Requisition r) {
        RequisitionResponse response = requisitionMapper.toDto(r);

        String materialID = r.getMaterialID();
        switch (r.getType()) {
            case RAW_MATERIAL:
                // set raw material
                rawMaterialRepository.findById(materialID).ifPresent(
                        rm -> response.setRawMaterial(
                                rawMaterialMapper.toDto(rm)
                        ));
                break;

            case SEMI_FINISH_PRODUCT:
                semiFinishProductRepository.findById(materialID).ifPresent(
                        sfp -> response.setSemiFinishProduct(
                                semiFinishProductMapper.toDto(sfp)
                        ));
                break;
        }
        return response;
    }

}
