package com.stock_mate.BE.controller.v1;

import com.stock_mate.BE.dto.request.RoleRequest;
import com.stock_mate.BE.dto.response.ResponseObject;
import com.stock_mate.BE.dto.response.RoleResponse;
import com.stock_mate.BE.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role", description = "API Quản lý chức vụ")
@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleV1Controller {

    @Autowired
    RoleService roleService;

    @GetMapping
    @Operation(summary = "Lấy danh sách chức vụ")
    public ResponseObject<Page<RoleResponse>> getAllRoles(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        Page<RoleResponse> list = roleService.getAll(search, page, size, sort);
        return ResponseObject.<Page<RoleResponse>>builder()
                .status(1000)
                .data(list)
                .message("Lấy danh sách role thành công")
                .build();
    }

    @GetMapping("/{name}")
    @Operation(summary = "Lấy thông tin chi tiết chức vụ theo tên")
    public ResponseObject<RoleResponse> getRoleByName(@PathVariable String name) {
        return ResponseObject.<RoleResponse>builder()
                .status(1000)
                .data(roleService.getRoleByName(name))
                .message("Lấy thông tin role thành công")
                .build();
    }

    @PostMapping
    @Operation(summary = "Tạo mới chức vụ")
    public ResponseObject<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ResponseObject.<RoleResponse>builder()
                .status(1000)
                .data(roleService.createRole(request))
                .message("Tạo role thành công")
                .build();
    }

    @PutMapping("/{name}")
    @Operation(summary = "Cập nhật chức vụ theo tên")
    public ResponseObject<RoleResponse> updateRole(
            @PathVariable String name,
            @RequestBody RoleRequest request) {
        return ResponseObject.<RoleResponse>builder()
                .status(1000)
                .data(roleService.updateRole(name, request))
                .message("Cập nhật role thành công")
                .build();
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "Xóa chức vụ theo tên")
    public ResponseObject<Boolean> deleteRole(@PathVariable String name) {
        return ResponseObject.<Boolean>builder()
                .status(1000)
                .data(roleService.deleteRole(name))
                .message("Xóa role thành công")
                .build();
    }
}