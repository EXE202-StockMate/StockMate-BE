package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.FinishProductRequest;
import com.stock_mate.BE.dto.request.FinishProductUpdateRequest;
import com.stock_mate.BE.dto.response.FinishProductResponse;
import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.FinishProductMedia;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.entity.Stock;
import com.stock_mate.BE.enums.FinishProductCategory;
import com.stock_mate.BE.enums.StockStatus;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.FinishProductMapper;
import com.stock_mate.BE.repository.FinishProductMediaRepository;
import com.stock_mate.BE.repository.FinishProductRepository;
import com.stock_mate.BE.repository.StockRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FinishProductService extends BaseSpecificationService<FinishProduct, FinishProductResponse> {
    @Autowired
    FinishProductRepository finishProductRepository;
    @Autowired
    FinishProductMapper finishProductMapper;
    @Autowired
    FinishProductMediaRepository mediaRepository;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    FinishProductMediaService mediaService;

    @Override
    protected JpaSpecificationExecutor<FinishProduct> getRepository() {
        return finishProductRepository;
    }

    @Override
    protected Function<FinishProduct, FinishProductResponse> getMapper() {
        return finishProductMapper::toDto;
    }

    @Override
    protected Specification<FinishProduct> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            return getPredicate(searchTerm, root, cb);
        };
    }

    private Predicate getPredicate(String searchTerm, Root<FinishProduct> root, CriteriaBuilder cb) {
        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        return cb.or(
                cb.like(cb.lower(root.get("name")), searchPattern),
                cb.like(cb.lower(root.get("description")), searchPattern),
                cb.like(cb.lower(root.get("category")), searchPattern),
                cb.like(cb.lower(root.get("fgID")), searchPattern),
                // Handle integer status field differently - convert to string or compare directly
                cb.equal(root.get("status"),
                        searchTerm.matches("\\d+") ? Integer.parseInt(searchTerm) : -1)
        );
    }

    @Transactional
    public FinishProductResponse createFinishProduct(FinishProductRequest request) {
        if(request.name() == null || request.name().isEmpty()){
            throw new AppException(ErrorCode.PRODUCT_NAME_REQUIRED, "Tên thành phẩm không được để trống");
        }
        if(request.category() == null){
            throw new AppException(ErrorCode.PRODUCT_CATEGORY_REQUIRED, "Danh mục thành phẩm không được để trống");
        }
        FinishProduct fp = finishProductMapper.toEntity(request);
        fp.setStatus(1);

        FinishProduct savedFinish = finishProductRepository.save(fp);

        //Tạo mới stock sau khi tạo finish product
        Stock stock = new Stock();
        stock.setQuantity(0);
        stock.setFinishProduct(savedFinish);
        //Để mặc định là Cái, sau này có thể cập nhật lại
        stock.setUnit("Cái");
        stock.setStatus(StockStatus.ACTIVE);
        stockRepository.save(stock);
        return finishProductMapper.toDto(savedFinish);
    }

    public FinishProductResponse updateFinishProduct(FinishProductUpdateRequest ureq) {
        FinishProduct fp = finishProductRepository.findById(ureq.fgID())
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND));
        if (StringUtils.hasText(ureq.name()) && !Objects.equals(fp.getName(), ureq.name())) {
            fp.setName(ureq.name());
        }
        if (StringUtils.hasText(ureq.description()) && !Objects.equals(fp.getDescription(), ureq.description())) {
            fp.setDescription(ureq.description());
        }
        if (StringUtils.hasText(ureq.dimension()) && !Objects.equals(fp.getDimension(), ureq.dimension())) {
            fp.setDimension(ureq.dimension());
        }
        if (ureq.category() != null &&  !Objects.equals(fp.getCategory(), ureq.category())) {
            fp.setCategory(ureq.category());
        }
        if (ureq.status() != null && fp.getStatus() != ureq.status()) {
            fp.setStatus(ureq.status());
        }
        return finishProductMapper.toDto(finishProductRepository.save(fp));
    }

    public boolean deleteFinishProduct(String fgID) {
        FinishProduct fp = finishProductRepository.findById(fgID)
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND));
        fp.setStatus(0); // xóa thì set status = 0
        finishProductRepository.save(fp);
        Stock stock = (Stock) stockRepository.findByFinishProduct_FgID(fgID);
        //set quantity = 0 và status = INACTIVE
        stock.setQuantity(0);
        stock.setStatus(StockStatus.INACTIVE);
        stockRepository.save(stock);
        return true;
    }

    public FinishProductResponse getFinishProductById(String finishProductId) {
        FinishProduct finishProduct = finishProductRepository.findById(finishProductId)
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND, "Không tìm thấy thành phẩm với id: " + finishProductId));
        List<FinishProductMedia> mediaList = mediaRepository.findByFinishProduct_FgID(finishProductId);
        finishProduct.setMediaList(mediaList);
        return finishProductMapper.toDto(finishProduct);
    }

    //Cập nhật nhều hình ảnh cho vật tư
    public List<FinishProductMedia> updateFPImages(String finishProductId, List<MultipartFile> files) throws Exception {
        FinishProduct finishProduct = finishProductRepository.findById(finishProductId)
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND, "Không tìm thấy thành phẩm với id: " + finishProductId));

        List<FinishProductMedia> savedMedia = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Upload vào Cloudinary  với folder
                String folder = "finish_products/" + finishProductId;
                String imageUrl = cloudinaryService.uploadImageWithFolder(file, folder);
                String publicId = cloudinaryService.extractPublicIdFromUrl(imageUrl);

                // Tạo rmedia entity
                FinishProductMedia media = new FinishProductMedia();
                media.setFinishProduct(finishProduct);
                media.setMediaUrl(imageUrl);
                media.setPublicId(publicId);
                media.setMediaType("IMAGE");

                // Lưu vào media repository
                savedMedia.add(mediaRepository.save(media));
            }
        }
        return savedMedia;
    }

    //Xóa hết hình ảnh của vật tư theo id
    @Transactional
    public void deleteFPImages(String finishProductId) throws IOException {
        // Lấy thông tin finish product
        FinishProduct finishProduct = finishProductRepository.findById(finishProductId)
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND, "Không tìm thấy thành phẩm với id: " + finishProductId));

        // Xóa hình ảnh từ Cloudinary
        List<FinishProductMedia> mediaList = new ArrayList<>(finishProduct.getMediaList());
        for (FinishProductMedia media : mediaList) {
            if (media.getPublicId() != null) {
                try {
                    cloudinaryService.deleteImage(media.getPublicId());
                } catch (Exception e) {
                    System.err.println("Error deleting image: " + e.getMessage());
                }
            }
        }

        // Xóa media bằng cách xóa khỏi collection
        finishProduct.getMediaList().clear();
        finishProductRepository.save(finishProduct);
    }


    // Ham sap xep
    private Sort createSort(String[] sort) {
        if (sort.length > 1) {
            String sortField = sort[0];
            String sortDirection = sort[1];
            return Sort.by(sortDirection.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        }
        return Sort.by(Sort.Direction.ASC, "name");
    }

    public FinishProduct findById(String finishProductId) {
        return finishProductRepository.findById(finishProductId)
                .orElseThrow(() -> new AppException(ErrorCode.FINISH_PRODUCT_NOT_FOUND));
    }

}
