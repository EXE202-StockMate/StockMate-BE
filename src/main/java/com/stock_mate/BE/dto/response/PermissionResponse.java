package com.stock_mate.BE.dto.response;

import com.stock_mate.BE.entity.Permission;

import java.util.Set;

public record PermissionResponse(
     String name,
     String description) {
}
