package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);
}
