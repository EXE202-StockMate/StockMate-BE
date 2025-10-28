package com.stock_mate.BE.dto.response;

import com.stock_mate.BE.entity.Permission;

import java.util.Set;

public record RoleResponse (
    String name,
    String description,
    Set<Permission> permissions) {
}
