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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
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
    public List<FinishProductResponse> getAllFinishProducts() {
        var list = finishProductRepository.findAll();

        list.forEach(rm -> {
            List<FinishProductMedia> mediaList =
                    mediaRepository.findByFinishProduct_FgID(rm.getFgID());
            rm.setMediaList(mediaList);
        });

        return list.stream().map(finishProductMapper::toDto).toList();
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
}
