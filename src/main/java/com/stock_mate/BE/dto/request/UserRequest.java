package com.stock_mate.BE.dto.request;

import com.stock_mate.BE.entity.Role;
import com.stock_mate.BE.entity.User;
import com.stock_mate.BE.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
public class UserRequest {
    String username;
    String password;
    String image;
    String userStatus;
    String roleName;
    String managerID;
}
