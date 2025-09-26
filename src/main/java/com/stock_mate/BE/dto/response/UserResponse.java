package com.stock_mate.BE.dto.response;

public record UserResponse(String userID, String fullName, String phoneNumber, String email, String image, String status) {
}
