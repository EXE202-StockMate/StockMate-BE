package com.stock_mate.BE.controller.v1;


import com.stock_mate.BE.dto.request.CustomerRequest;
import com.stock_mate.BE.dto.request.UserRequest;
import com.stock_mate.BE.dto.response.CustomerResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

//    @GetMapping
//    public ResponseObject<List<UserResponse>> getAllUsers() {
//        var users = userService.getAllUsers();
//        return ResponseObject.<List<UserResponse>>builder()
//                .status(1000)
//                .data(users)
//                .message("Get all users successfully")
//                .build();
//    }

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

    @GetMapping
    @Operation(summary = "Lấy danh sách nhân viên")
    public ResponseObject<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createDate,desc") String[] sort
    ) {
        Page<UserResponse> list = userService.getAll(search, page, size, sort);
        return ResponseObject.<Page<UserResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách nhân viên thành công")
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết nhân viên theo ID")
    public ResponseObject<UserResponse> getUserById(@PathVariable String id) {
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(userService.getUserById(id))
                .message("Lấy thông tin nhân viên thành công")
                .build();
    }

    @PostMapping
    @Operation(summary = "Tạo mới nhân viên")
    public ResponseObject<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(userService.createUser(request))
                .message("Tạo nhân viên thành công")
                .build();
    }


    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseObject<UserResponse> updateUser(
            @PathVariable String id,
            @RequestBody UserRequest request) {
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(userService.updateUser(id, request))
                .message("Cập nhật nhân viên thành công")
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa nhân viên")
    public ResponseObject<Boolean> deleteUser(@PathVariable String id) {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .data(userService.deleteUser(id))
                .message("Xóa nhân viên thành công")
                .build();
    }
}
