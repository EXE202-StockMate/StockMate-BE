package com.stock_mate.BE.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stock_mate.BE.entity.Role;
import com.stock_mate.BE.enums.UserStatus;
import org.apache.catalina.Manager;

import java.time.LocalDate;

public record UserResponse(
        String userID,
        String username,
        String image,
        UserStatus status,
        String password,
        String role,
        String manager,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate createDate,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate updateDate) {
}
