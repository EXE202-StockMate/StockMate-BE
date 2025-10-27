package com.stock_mate.BE.service;

import com.stock_mate.BE.dto.request.RoleRequest;
import com.stock_mate.BE.entity.Permission;
import com.stock_mate.BE.entity.Role;
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
            throw new RuntimeException("Chức vụ này đã tồn tại");
        }
        for (Permission permission : request.getPermissions()) {
            if (!permissionRepository.existsById(permission.getName())) {
                permissionRepository.save(permission);
            }
        }
        return roleMapper.toResponse(roleRepository.save(roleMapper.toEntity(request)));
    }

    @Transactional
    public RoleResponse updateRole(String name, RoleRequest request) {
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new ProviderNotFoundException("Không tìm thấy chức vụ" + name));

        if(request.getName() != null
           && !request.getName().trim().isEmpty()
           && !request.getName().equals(role.getName())) {
            role.setName(name);
        }
        if(request.getDescription() != null
           && !request.getDescription().trim().isEmpty()
           && !request.getDescription().equals(role.getDescription())) {
            role.setDescription(request.getDescription());
        }
        if (request.getPermissions() != null) {
            // Copy danh sách permissions hiện có trong role
            Set<Permission> currentPermissions = new HashSet<>(role.getPermissions());
            for (Permission permission : request.getPermissions()) {
                boolean alreadyHas = currentPermissions.stream()
                        .anyMatch(p -> p.getName().equals(permission.getName()));
                if (!alreadyHas) {
                    Permission existing = permissionRepository.findById(permission.getName())
                            .orElseGet(() -> permissionRepository.save(permission));
                    role.getPermissions().add(existing);
                }
            }
        }
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Transactional
    public boolean deleteRole(String name) {
        if (!roleRepository.existsById(name)) {
            throw new ProviderNotFoundException("Không tìm thấy chức vụ" + name);
        }
        roleRepository.deleteById(name);
        return true;
    }
}
