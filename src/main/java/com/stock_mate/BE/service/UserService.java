package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.entity.User;
import com.stock_mate.BE.mapper.UserMapper;
import com.stock_mate.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    public List<UserResponse> getAllUsers() {
        var list = userRepository.findAll();
        return list.stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public UserResponse updateUserImage(String userId, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete old image if exists
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            // Extract public ID from the existing image URL
            String oldPublicId = cloudinaryService.extractPublicIdFromUrl(user.getImage());
            if (oldPublicId != null) {
                cloudinaryService.deleteImage(oldPublicId);
            }
        }

        // Upload new image
        String folder = "users/" + userId;
        String imageUrl = cloudinaryService.uploadImageWithFolder(imageFile, folder);

        // Update user entity
        user.setImage(imageUrl);
        user.setUpdateDate(LocalDate.now());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}
