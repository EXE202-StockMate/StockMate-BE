package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record UserResponse(
        String userID,
        String fullName,
        String phoneNumber,
        String email,
        String image,
        String status,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate createDate,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate updateDate) {
}
