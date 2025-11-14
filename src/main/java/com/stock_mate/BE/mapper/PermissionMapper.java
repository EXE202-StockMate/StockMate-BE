package com.stock_mate.BE.mapper;

import com.stock_mate.BE.dto.request.PermissionRequest;
import com.stock_mate.BE.dto.request.RoleRequest;
import com.stock_mate.BE.dto.response.PermissionResponse;
import com.stock_mate.BE.dto.response.RoleResponse;
import com.stock_mate.BE.entity.Permission;
import com.stock_mate.BE.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponse toResponse(Permission permission);
    Set<PermissionResponse> toResponses(Set<Permission> permissions);

}