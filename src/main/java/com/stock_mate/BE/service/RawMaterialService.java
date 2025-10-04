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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    //Lấy tất cả vật tư
    public List<RawMaterialResponse> getAllRawMaterials() {
        var list = rawMaterialRepository.findAll();

        list.forEach(rm -> {
            List<RawMaterialMedia> mediaList =
                    mediaRepository.findByRawMaterial_RmID(rm.getRmID());
            rm.setMediaList(mediaList);
        });

        return list.stream().map(rawMaterialMapper::toDto).toList();
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
}
