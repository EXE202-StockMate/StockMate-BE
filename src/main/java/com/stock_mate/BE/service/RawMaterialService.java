package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.RawMaterialUpdateRequest;
import com.stock_mate.BE.dto.response.RawMaterialResponse;

import com.stock_mate.BE.dto.request.RawMaterialRequest;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.RawMaterialMapper;
import com.stock_mate.BE.repository.RawMaterialMediaRepository;
import com.stock_mate.BE.repository.RawMaterialRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RawMaterialService extends BaseSpecificationService<RawMaterial, RawMaterialResponse> {

    @Autowired
    RawMaterialRepository rawMaterialRepository;
    @Autowired
    RawMaterialMapper rawMaterialMapper;
    @Autowired
    RawMaterialMediaRepository mediaRepository;
    @Autowired
    CloudinaryService cloudinaryService;

    @Override
    protected JpaSpecificationExecutor<RawMaterial> getRepository() {
        return rawMaterialRepository;
    }

    @Override
    protected Function<RawMaterial, RawMaterialResponse> getMapper() {
        return  rawMaterialMapper::toDto;
    }

    @Override
    protected Specification<RawMaterial> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern),
                    cb.like(cb.lower(root.get("category")), searchPattern),
                    cb.like(cb.lower(root.get("rmID")), searchPattern),
                    cb.like(cb.lower(root.get("code")), searchPattern),
                    cb.like(cb.lower(root.get("dimension")), searchPattern),
                    // Handle integer status field correctly
                    searchTerm.matches("\\d+") ?
                            cb.equal(root.get("status"), Integer.parseInt(searchTerm)) :
                            cb.or() // Empty predicate that will be ignored if not a number
            );
        };
    }

    @Transactional
    public boolean deleteRawMaterial(String rmID){
        RawMaterial rm = rawMaterialRepository.findById(rmID)
                .orElseThrow(() -> new ProviderNotFoundException("Raw Material Not Found"));
        rawMaterialRepository.delete(rm);
        return true;
    }

    @Transactional
    public RawMaterialResponse createRawMaterial(RawMaterialRequest request) {
        if(request.name() == null || request.name().isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NAME_REQUIRED, "Tên vật tư không được để trống");
        }if(request.category() == null){
            throw new AppException(ErrorCode.PRODUCT_CATEGORY_REQUIRED, "Danh mục vật tư không được để trống");
        }
            RawMaterial raw = rawMaterialMapper.toEntity(request);
            raw.setStatus(1);
            return rawMaterialMapper.toDto(rawMaterialRepository.save(raw));

    }

    @Transactional
    public RawMaterialResponse updateRawMaterial(RawMaterialUpdateRequest request) {
        RawMaterial raw = rawMaterialRepository.findById(request.rmID())
                        .orElseThrow(() -> new ProviderNotFoundException("Raw Material not found"));
        
        if (StringUtils.hasText(request.name()) && !Objects.equals(raw.getName(), request.name())) {
            raw.setName(request.name());
        }

        if (StringUtils.hasText(request.code()) && !Objects.equals(raw.getCode(), request.code())) {
            raw.setCode(request.code());
        }

        if (StringUtils.hasText(request.description()) && !Objects.equals(raw.getDescription(), request.description())) {
            raw.setDescription(request.description());
        }

        if (request.category() != null && !Objects.equals(raw.getCategory(), request.category())) {
            raw.setCategory(request.category());
        }

        if (StringUtils.hasText(request.dimension()) && !Objects.equals(raw.getDimension(), request.dimension())) {
            raw.setDimension(request.dimension());
        }

        if (request.thickness() != null && !Objects.equals(raw.getThickness(), request.thickness())) {
            raw.setThickness(request.thickness());
        }

        if (request.status() != null && raw.getStatus() != request.status()) {
            raw.setStatus(request.status());
        }

        raw.setUpdateDate(LocalDate.now());

        return rawMaterialMapper.toDto(rawMaterialRepository.save(raw));
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

    public RawMaterialResponse findById(String rmID){
        RawMaterial raw = rawMaterialRepository.findById(rmID)
                .orElseThrow(() -> new ProviderNotFoundException("Raw material not found"));
        return rawMaterialMapper.toDto(raw);
    }

    //Xóa hết hình ảnh của vật tư theo id
    @Transactional
    public void deleteRMImages(String materialId) throws IOException {
        // Lấy thông tin raw material
        RawMaterial rawMaterial = rawMaterialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư"));
        // Delete from Cloudinary (you'd need to fetch and delete each public_id)
        List<RawMaterialMedia> mediaList = new ArrayList<>(rawMaterial.getMediaList());
        for (RawMaterialMedia media : mediaList) {
            if (media.getPublicId() != null) {
                try {
                    cloudinaryService.deleteImage(media.getPublicId());
                } catch (Exception e) {
                    System.err.println("Error deleting image: " + e.getMessage());
                }
            }
        }

        // Xóa media bằng cách xóa khỏi collection
        rawMaterial.getMediaList().clear();
        rawMaterialRepository.save(rawMaterial);
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

}
