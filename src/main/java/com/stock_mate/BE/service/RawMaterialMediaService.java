package com.stock_mate.BE.service;

import com.stock_mate.BE.entity.RawMaterial;
import com.stock_mate.BE.entity.RawMaterialMedia;
import com.stock_mate.BE.repository.RawMaterialMediaRepository;
import com.stock_mate.BE.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialMediaService {

    @Autowired
    RawMaterialMediaRepository mediaRepository;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    CloudinaryService cloudinaryService;

    public List<RawMaterialMedia> getMediaByRawMaterialId(String rawMaterialId) {
        return mediaRepository.findByRawMaterial_RmID(rawMaterialId);
    }

    @Transactional
    public RawMaterialMedia addMedia(String rawMaterialId, MultipartFile file, String mediaType, String description) throws IOException {
        RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Raw material not found"));

        String folder = "raw_materials/" + rawMaterialId;
        String mediaUrl = cloudinaryService.uploadImageWithFolder(file, folder);
        String publicId = cloudinaryService.extractPublicIdFromUrl(mediaUrl);

        RawMaterialMedia media = new RawMaterialMedia();
        media.setRawMaterial(rawMaterial);
        media.setMediaUrl(mediaUrl);
        media.setMediaType(mediaType);
        media.setDescription(description);
        media.setPublicId(publicId);

        return mediaRepository.save(media);
    }

    @Transactional
    public void deleteMedia(Long mediaId) throws IOException {
        RawMaterialMedia media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (media.getPublicId() != null) {
            cloudinaryService.deleteImage(media.getPublicId());
        }

        mediaRepository.delete(media);
    }

}
