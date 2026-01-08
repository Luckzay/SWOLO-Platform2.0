package com.swole.platform.service.impl;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.Permission;
import com.swole.platform.repository.PermissionRepository;
import com.swole.platform.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission updatePermission(Long id, Permission permission) {
        Permission existingPermission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
        
        permission.setId(id);
        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        Permission existingPermission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", id));
        
        permissionRepository.deleteById(id);
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        return permissionRepository.findByRoleId(roleId);
    }
}