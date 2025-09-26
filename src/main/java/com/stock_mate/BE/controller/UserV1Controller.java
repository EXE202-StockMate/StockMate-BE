package com.stock_mate.BE.controller;


import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/v1/users")
public class UserV1Controller {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseObject getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseObject.builder()
                .status(1000)
                .data(users)
                .message("Get all users successfully")
                .build();
    }
}
