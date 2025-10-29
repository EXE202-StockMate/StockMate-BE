package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.RoleRequest;
import com.stock_mate.BE.dto.response.RoleResponse;
import com.stock_mate.BE.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);
    Role toEntity(RoleRequest request);
    List<RoleResponse> toRoles(List<Role> role);


}
