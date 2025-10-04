package com.stock_mate.BE.service;

import com.cloudinary.api.exceptions.NotFound;
import com.stock_mate.BE.dto.response.RawMaterialResponse;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.mapper.RawMaterialMapper;
import com.stock_mate.BE.repository.RawMaterialMediaRepository;
import com.stock_mate.BE.repository.RawMaterialRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class RawMaterialService {

    @Autowired
    RawMaterialRepository rawMaterialRepository;
    @Autowired
    RawMaterialMapper rawMaterialMapper;
    @Autowired
    RawMaterialMediaRepository mediaRepository;
    @Autowired
    CloudinaryService cloudinaryService;

    //Lấy tất cả vật tư với phân trang và tìm kiếm
    public Page<RawMaterialResponse> getAllRawMaterials(
            String search,
            int page,
            int size,
            String[] sort) {

        // Chuyển đổi từ page bắt đầu từ 1 sang page bắt đầu từ 0
        int zeroBasedPage = Math.max(0, page - 1);

        // Tạo đối tượng Sort
        Sort sortObj = createSort(sort);

        // Tạo Pageable
        Pageable pageable = PageRequest.of(zeroBasedPage, size, sortObj);

        Page<RawMaterial> materialPage;

        if (search != null && !search.trim().isEmpty()) {
            // Tạo specification cho tìm kiếm
            Specification<RawMaterial> spec = (root, query, cb) -> {
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
                        cb.like(cb.lower(root.get("rmID")), searchPattern),
                        cb.like(cb.lower(root.get("code")), searchPattern),
                        cb.like(cb.lower(root.get("dimension")), searchPattern),
                        cb.like(cb.function("cast", String.class, root.get("status")), searchPattern)
                );
            };
            materialPage = rawMaterialRepository.findAll(spec, pageable);
        } else {
            materialPage = rawMaterialRepository.findAll(pageable);
        }

        // Map sang DTO và tải media cho mỗi vật liệu
        return materialPage.map(material -> {
            List<RawMaterialMedia> mediaList =
                    mediaRepository.findByRawMaterial_RmID(material.getRmID());
            material.setMediaList(mediaList);
            return rawMaterialMapper.toDto(material);
        });
    }

    //Cập nhật nhều hình ảnh cho vật tư
    public List<RawMaterialMedia> updateRMImages(String materialId, List<MultipartFile> files) throws Exception {
        RawMaterial rawMaterial = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ProviderNotFoundException("Raw material not found"));

        List<RawMaterialMedia> savedMedia = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // Upload vào Cloudinary  với folder
                String folder = "raw_materials/" + materialId;
                String imageUrl = cloudinaryService.uploadImageWithFolder(file, folder);
                String publicId = cloudinaryService.extractPublicIdFromUrl(imageUrl);

                // Tạo rmedia entity
                RawMaterialMedia media = new RawMaterialMedia();
                media.setRawMaterial(rawMaterial);
                media.setMediaUrl(imageUrl);
                media.setPublicId(publicId);media.setMediaType("IMAGE");

                // Lưu vào media repository
                savedMedia.add(mediaRepository.save(media));
            }
        }
        return savedMedia;
    }

    //Xóa hết hình ảnh của vật tư theo id
    public void deleteRMImages(String materialId) throws IOException {
        // Delete from Cloudinary (you'd need to fetch and delete each public_id)
        List<RawMaterialMedia> mediaList = mediaRepository.findByRawMaterial_RmID(materialId);
        for (RawMaterialMedia media : mediaList) {
            if (media.getPublicId() != null) {
                cloudinaryService.deleteImage(media.getPublicId());
            }
        }

        // Delete from database
        mediaRepository.deleteByRawMaterial_RmID(materialId);
    }

    // Hàm sắp xếp
    private Sort createSort(String[] sort) {
        if (sort.length > 1) {
            String sortField = sort[0];
            String sortDirection = sort[1];
            return Sort.by(sortDirection.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        }
        return Sort.by(Sort.Direction.ASC, "name");
    }

    // Hàm kiểm tra định dạng ngày tháng
    private boolean isDateFormat(String input) {
        // Kiểm tra định dạng dd-MM-yyyy
        return input.matches("\\d{2}-\\d{2}-\\d{4}");
    }
}
