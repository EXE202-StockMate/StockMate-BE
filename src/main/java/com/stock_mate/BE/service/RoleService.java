package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.PermissionRequest;
import com.stock_mate.BE.dto.request.RoleRequest;
import com.stock_mate.BE.entity.Permission;
import com.stock_mate.BE.entity.Role;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.RoleMapper;
import com.stock_mate.BE.dto.response.RoleResponse;
import com.stock_mate.BE.repository.PermissionRepository;
import com.stock_mate.BE.repository.RoleRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.nio.file.ProviderNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService extends BaseSpecificationService <Role, RoleResponse> {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    protected JpaSpecificationExecutor<Role> getRepository() {
        return roleRepository;
    }

    @Override
    protected Function<Role, RoleResponse> getMapper() {
        return roleMapper::toResponse;
    }

    @Override
    protected Specification<Role> buildSpecification(String searchTerm) {
        return (root, query, cb) -> {
            //if searchTerm is null => no condition
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String search = searchTerm.trim();

            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern)
            );
        };
    }

    public RoleResponse getRoleByName(String name) {
        return roleMapper.toResponse(roleRepository.findByName(name));
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsById(request.getName())) {
            throw new AppException(ErrorCode.ROLE_EXISTS, "Chức vụ đã tồn tại: " + request.getName());
        }
        for (PermissionRequest request1 : request.getPermissions()) {
            Permission permission = new Permission();
            if (!permissionRepository.existsById(request1.getName())) {
                permission.setName(request1.getName());
                permission.setDescription(request1.getDescription());
                permissionRepository.save(permission);
            }
        }
        return roleMapper.toResponse(roleRepository.save(roleMapper.toEntity(request)));
    }

    @Transactional
    public RoleResponse updateRole(String name, RoleRequest request) {
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy chức vụ: " + name));
        if(request.getDescription() != null
           && !request.getDescription().trim().isEmpty()
           && !request.getDescription().equals(role.getDescription())) {
            role.setDescription(request.getDescription());
        }
        if (request.getPermissions() != null) {
            // Copy danh sách permissions hiện có trong role
            Set<Permission> newPermissions = new HashSet<>();

            for (PermissionRequest request1 : request.getPermissions()) {
                Permission permission = permissionRepository.findById(request1.getName())
                        .orElseGet(() -> {
                            Permission newPerm = new Permission();
                            newPerm.setName(request1.getName());
                            newPerm.setDescription(request1.getDescription());
                            return permissionRepository.save(newPerm);
                        });
                newPermissions.add(permission);
            }
            role.getPermissions().clear();
            role.setPermissions(newPermissions);
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Transactional
    public boolean deleteRole(String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND, "Không tìm thấy chức vụ: " + name);
        }
        // Xóa liên kết trước
        role.getPermissions().clear();
        roleRepository.save(role);  // Cập nhật bảng trung gian
        roleRepository.deleteById(name);
        return true;
    }
}
