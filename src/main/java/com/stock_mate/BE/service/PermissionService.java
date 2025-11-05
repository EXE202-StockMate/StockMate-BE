package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.PermissionRequest;
import com.stock_mate.BE.dto.response.PermissionResponse;
import com.stock_mate.BE.entity.Permission;
import com.stock_mate.BE.exception.AppException;
import com.stock_mate.BE.exception.ErrorCode;
import com.stock_mate.BE.mapper.PermissionMapper;
import com.stock_mate.BE.repository.PermissionRepository;
import com.stock_mate.BE.service.filter.BaseSpecificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService extends BaseSpecificationService<Permission, PermissionResponse> {

    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    PermissionMapper permissionMapper;

    @Override
    protected JpaSpecificationExecutor<Permission> getRepository() {
        return permissionRepository;
    }

    @Override
    protected Function<Permission, PermissionResponse> getMapper() {
        return permissionMapper:: toResponse;
    }

    @Override
    protected Specification<Permission> buildSpecification(String searchTerm) {
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

    public PermissionResponse getPermissionByName(String name) {
        return permissionMapper.toResponse(permissionRepository.findByName(name));
    }

    @Transactional
    public Set<PermissionResponse> createPermission(PermissionRequest request) {
        for (Permission permission : request.getPermissions()) {
            if (permissionRepository.existsById(permission.getName())) {
                //throw new AppException(ErrorCode.PERMISSION_EXISTS, "Permission đã tồn tại: " + permission.getName());
                request.getPermissions().remove(permission);
            } else {
                permissionRepository.save(permission);
            }
        }
        return permissionMapper.toResponses(request.getPermissions());
    }

    @Transactional
    public PermissionResponse updatePermission(String name, String des) {
        Permission permission = permissionRepository.findByName(name);
        if (permission == null) {
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND);
        }
        if (des != null && !des.equals(permission.getDescription())) {
            permission.setDescription(des);
        }
        return permissionMapper.toResponse(permission);
    }


    @Transactional
    public boolean deletePermission(String name) {
        if (!permissionRepository.existsById(name)) {
            throw new AppException(ErrorCode.PERMISSION_NOT_FOUND, "Không tìm thấy permission: " + name);
        }
        permissionRepository.deleteById(name);
        return true;
    }
}
