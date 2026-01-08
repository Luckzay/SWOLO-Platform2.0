package com.swole.platform.controller.statistics;

import com.swole.platform.dto.ExperimentStatisticsDto;
import com.swole.platform.model.entity.User;
import com.swole.platform.service.UserService;
import com.swole.platform.service.statistics.ExperimentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/statistics/users")
public class UserStatisticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExperimentStatisticsService experimentStatisticsService;

    // 获取用户总数
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(count);
    }

    // 获取特定用户的所有实验统计
    @GetMapping("/{userId}/experiments")
    public ResponseEntity<List<ExperimentStatisticsDto>> getUserExperiments(@PathVariable Long userId) {
        List<ExperimentStatisticsDto> stats = experimentStatisticsService.getUserStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    // 获取按角色分组的用户列表
    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Long roleId) {
        List<User> users = userService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }
    
    // 获取所有用户统计
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}