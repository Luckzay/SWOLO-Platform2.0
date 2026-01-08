package com.swole.platform.service;

import com.swole.platform.model.entity.Permission;
import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<Permission> getAllPermissions();
    Optional<Permission> getPermissionById(Long id);
    Permission createPermission(Permission permission);
    Permission updatePermission(Long id, Permission permission);
    void deletePermission(Long id);
    List<Permission> findByRoleId(Long roleId);
}