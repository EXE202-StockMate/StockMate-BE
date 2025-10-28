package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.entity.Role;
import com.stock_mate.BE.entity.User;
import com.stock_mate.BE.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String fullName;
    String phoneNumber;
    String email;
    String password;
    String image;
    String userStatus;
    String roleName;
    String managerID;
}
