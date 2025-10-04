package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.response.FinishProductResponse;
import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.FinishProductMedia;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.mapper.FinishProductMapper;
import com.stock_mate.BE.repository.FinishProductMediaRepository;
import com.stock_mate.BE.repository.FinishProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinishProductService {
    @Autowired
    FinishProductRepository finishProductRepository;
    @Autowired
    FinishProductMapper finishProductMapper;
    @Autowired
    FinishProductMediaRepository mediaRepository;
    @Autowired
    CloudinaryService cloudinaryService;

    //Lấy tất cả vật tư
    public Page<FinishProductResponse> getAllFinishProducts(
            String search,
            int page,
            int size,
            String[] sort) {

        // Create sort object
        Sort sortObj = createSort(sort);

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<FinishProduct> productPage;

        if (search != null && !search.trim().isEmpty()) {
            // Create specification for search
            Specification<FinishProduct> spec = (root, query, cb) -> {
                String searchTerm = search.trim();

                // Kiểm tra xem search có phải là ngày tháng hay không
                if (isDateFormat(searchTerm)) {
                    try {
                        // Nếu là ngày, tạo điều kiện tìm kiếm theo ngày
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date searchDate = dateFormat.parse(searchTerm);

                        // Tạo khoảng thời gian cho cả ngày (từ 00:00:00 đến 23:59:59)
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(searchDate);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        Date startDate = calendar.getTime();

                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        Date endDate = calendar.getTime();

                        return cb.or(
                                cb.between(root.get("createDate"), startDate, endDate),
                                cb.between(root.get("updateDate"), startDate, endDate)
                        );
                    } catch (ParseException e) {
                        // Nếu parse lỗi, quay lại tìm kiếm bình thường
                    }
                }

                // Tìm kiếm theo chuỗi bình thường cho các trường khác
                String searchPattern = "%" + searchTerm.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), searchPattern),
                        cb.like(cb.lower(root.get("description")), searchPattern),
                        cb.like(cb.lower(root.get("category")), searchPattern),
                        cb.like(cb.lower(root.get("fgID")), searchPattern),
                        cb.like(cb.lower(root.get("status")), searchPattern)
                );
            };
            productPage = finishProductRepository.findAll(spec, pageable);
        } else {
            productPage = finishProductRepository.findAll(pageable);
        }

        // Map to DTOs and load media for each product
        return productPage.map(product -> {
            List<FinishProductMedia> mediaList =
                    mediaRepository.findByFinishProduct_FgID(product.getFgID());
            product.setMediaList(mediaList);
            return finishProductMapper.toDto(product);
        });
    }

    //Cập nhật nhều hình ảnh cho vật tư
    public List<FinishProductMedia> updateFPImages(String finishProductId, List<MultipartFile> files) throws Exception {
        FinishProduct finishProduct = finishProductRepository.findById(finishProductId)
                .orElseThrow(() -> new ProviderNotFoundException("Finish product not found"));

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
                media.setPublicId(publicId);media.setMediaType("IMAGE");

                // Lưu vào media repository
                savedMedia.add(mediaRepository.save(media));
            }
        }
        return savedMedia;
    }

    //Xóa hết hình ảnh của vật tư theo id
    public void deleteFPImages(String finishProductId) throws IOException {
        // Delete from Cloudinary (you'd need to fetch and delete each public_id)
        List<FinishProductMedia> mediaList = mediaRepository.findByFinishProduct_FgID(finishProductId);
        for (FinishProductMedia media : mediaList) {
            if (media.getPublicId() != null) {
                cloudinaryService.deleteImage(media.getPublicId());
            }
        }

        // Delete from database
        mediaRepository.deleteByFinishProduct_FgID(finishProductId);
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

    // Ham kiem tra dinh dang ngay thang
    private boolean isDateFormat(String input) {
        // Kiểm tra định dạng yyyy-MM-dd
        return input.matches("\\d{2}-\\d{2}-\\d{4}");
    }
}
