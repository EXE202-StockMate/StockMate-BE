package com.stock_mate.BE.service;

import com.stock_mate.BE.entity.FinishProduct;
import com.stock_mate.BE.entity.FinishProductMedia;
import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.repository.FinishProductMediaRepository;
import com.stock_mate.BE.repository.FinishProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinishProductMediaService {

    @Autowired
    FinishProductMediaRepository mediaRepository;

    @Autowired
    FinishProductRepository finishProductRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    public List<FinishProductMedia> getMediaByFinishProductMedia(String finishProductId) {
        return mediaRepository.findByFinishProduct_FgID(finishProductId);
    }

    @Transactional
    public FinishProductMedia addMedia(String fgID, MultipartFile file, String mediaType, String description) throws IOException {
        FinishProduct finishProduct = finishProductRepository.findById(fgID)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phẩm với id: " + fgID));

        String folder = "finish_products/" + fgID;
        String mediaUrl = cloudinaryService.uploadImageWithFolder(file, folder);
        String publicId = cloudinaryService.extractPublicIdFromUrl(mediaUrl);

        FinishProductMedia media = new FinishProductMedia();
        media.setFinishProduct(finishProduct);
        media.setMediaUrl(mediaUrl);
        media.setMediaType(mediaType);
        media.setDescription(description);
        media.setPublicId(publicId);

        //lưu media #1 vào image của finishProduct
        finishProduct.setImage(finishProduct.getMediaList().get(0).getMediaUrl());
        finishProductRepository.save(finishProduct);

        return mediaRepository.save(media);
    }

    @Transactional
    public void deleteMedia(Long mediaId) throws IOException {
        FinishProductMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (media.getPublicId() != null) {
            cloudinaryService.deleteImage(media.getPublicId());
        }

        mediaRepository.delete(media);
    }
}
