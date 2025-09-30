package com.stock_mate.BE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("url");
    }

    public String uploadImageWithFolder(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("folder", folder));
        return (String) uploadResult.get("url");
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String extractPublicIdFromUrl(String imageUrl) {
        // Extract public ID from URL (e.g., "https://res.cloudinary.com/your-cloud/image/upload/v1234/folder/image.jpg")
        int uploadIndex = imageUrl.indexOf("upload/");
        if (uploadIndex != -1) {
            String publicIdWithExtension = imageUrl.substring(uploadIndex + 7);
            // Remove file extension if present
            int extensionIndex = publicIdWithExtension.lastIndexOf(".");
            if (extensionIndex != -1) {
                return publicIdWithExtension.substring(0, extensionIndex);
            }
            return publicIdWithExtension;
        }
        return null;
    }

}
