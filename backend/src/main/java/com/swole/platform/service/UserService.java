package com.swole.platform.service;

import com.swole.platform.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
    
    // 用户统计相关方法
    long getUserCount();
    List<User> getUsersByRoleId(Long roleId);
}