package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.UserRequest;
import com.stock_mate.BE.dto.response.UserResponse;
import com.stock_mate.BE.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "manager", source = "manager.username")
    UserResponse toDto(User user);

    User toEntity(UserRequest request);
}
