package com.stock_mate.BE.controller.v1;


import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "User", description = "API Người dùng")
@RestController
@RequestMapping("/v1/users")
public class UserV1Controller {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseObject<List<UserResponse>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseObject.<List<UserResponse>>builder()
                .status(1000)
                .data(users)
                .message("Get all users successfully")
                .build();
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseObject updateUserImage(
                @PathVariable String id,
                @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
             var updatedUser = userService.updateUserImage(id, imageFile);
            return ResponseObject.<UserResponse>builder()
                    .status(1000)
                    .data(updatedUser)
                    .message("User image updated successfully")
                    .build();
        }
}
