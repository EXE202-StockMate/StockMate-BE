package com.stock_mate.BE.controller.v1;

import com.nimbusds.jose.JOSEException;
import com.stock_mate.BE.dto.request.AuthenticationRequest;
import com.stock_mate.BE.dto.request.ResetPasswordRequest;
import com.stock_mate.BE.dto.response.AuthenticationResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.service.AuthenticationService;
import com.stock_mate.BE.service.CustomerService;
import com.stock_mate.BE.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Tag(name = "Authentication", description = "Authentication APIs")
@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationV1Controller {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseObject<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseObject.<AuthenticationResponse>builder()
                .status(1000)
                .data(authenticationService.authenticate(request))
                .message("Login successfully!!")
                .build();
    }

//    @PostMapping("/logout")
//    ResponseObject logout(@RequestParam String token)
//            throws ParseException, JOSEException {
//        authenticationService.logout(token);
//        return ResponseObject.builder()
//                .status(1000)
//                .message("Logout succesfully")
//                .build();
//    }

    @PostMapping("/introspect")
    ResponseObject<Boolean> authenticate(@RequestParam String token) throws ParseException, JOSEException {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .data(authenticationService.introspect(token))
                .build();
    }

    @PostMapping("/password-reset")
    public ResponseObject<Boolean> resetPassword(@RequestBody ResetPasswordRequest request)
            throws ParseException, JOSEException {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .message("Password reset successfully")
                .data(authenticationService.resetPassword(request))
                .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Lấy chi tiết nhân viên bằng token")
    public ResponseObject<UserResponse> getUserByToken(@RequestParam String token) throws ParseException, JOSEException {
        return ResponseObject.<UserResponse>builder()
                .status(1000)
                .data(authenticationService.getUserByToken(token))
                .message("Lấy thông tin nhân viên thành công")
                .build();
    }
}
