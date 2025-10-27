package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.LoginRequest;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "API authentication")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthV1Controller {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập cơ bản (không token)")
    public ResponseObject<UserResponse> basicLogin(@RequestBody LoginRequest request) {
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(authService.login(request))
                .message("Đăng nhập thành công")
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất cơ bản (theo email)")
    public ResponseObject<Boolean> basicLogout(@RequestParam String email) {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .data(authService.logout(email))
                .message("Đăng xuất thành công")
                .build();
    }
}
