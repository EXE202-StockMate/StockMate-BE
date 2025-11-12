package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.PermissionRequest;
import com.stock_mate.BE.dto.request.RoleRequest;
import com.stock_mate.BE.dto.response.PermissionResponse;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.RoleResponse;
import com.stock_mate.BE.entity.Permission;
import com.stock_mate.BE.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Permission", description = "API Quản lý permissions")
@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
public class PermissionV1Controller {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Lấy danh sách permisison")
    public ResponseObject<Page<PermissionResponse>> getAllPermissions(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        Page<PermissionResponse> list = permissionService.getAll(search, page, size, sort);
        return ResponseObject.<Page<PermissionResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách permission thành công")
                .build();
    }

    @GetMapping("/{name}")
    @Operation(summary = "Lấy thông tin chi tiết permission theo tên")
    public ResponseObject<PermissionResponse> getPermissionByName(@PathVariable String name) {
        return ResponseObject.<PermissionResponse>builder()
                .status(1000)
                .data(permissionService.getPermissionByName(name))
                .message("Lấy thông tin permission thành công")
                .build();
    }

    @PostMapping
    @Operation(summary = "Tạo mới permisison")
    public ResponseObject<PermissionResponse> createRole(@RequestBody PermissionRequest request) {
        return ResponseObject.<PermissionResponse>builder()
                .status(1000)
                .data(permissionService.createPermission(request))
                .message("Tạo permission thành công")
                .build();
    }

    @PutMapping("/{name}")
    @Operation(summary = "Cập nhật permisson theo tên")
    public ResponseObject<PermissionResponse> updatePermission(
            @PathVariable String name,
            @RequestParam(required = false) String des) {
        return ResponseObject.<PermissionResponse>builder()
                .status(1000)
                .data(permissionService.updatePermission(name, des))
                .message("Cập nhật permission thành công")
                .build();
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Xóa permission theo tên")
    public ResponseObject<Boolean> deletePermission(@PathVariable String name) {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .data(permissionService.deletePermission(name))
                .message("Xóa permission thành công")
                .build();
    }
}
