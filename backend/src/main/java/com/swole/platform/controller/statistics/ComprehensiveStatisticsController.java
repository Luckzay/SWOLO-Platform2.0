package com.swole.platform.controller.statistics;

import com.swole.platform.dto.ExperimentStatisticsDto;
import com.swole.platform.model.entity.User;
import com.swole.platform.service.UserService;
import com.swole.platform.service.statistics.ExperimentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/statistics/comprehensive")
public class ComprehensiveStatisticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExperimentStatisticsService experimentStatisticsService;

    // 获取综合统计信息
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getComprehensiveSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // 用户统计数据
        long totalUsers = userService.getUserCount();
        summary.put("totalUsers", totalUsers);
        
        // 实验统计数据
        ExperimentStatisticsDto overallStats = experimentStatisticsService.getOverallStatistics();
        summary.put("overallExperimentStats", overallStats);
        
        return ResponseEntity.ok(summary);
    }

    // 获取用户活动统计
    @GetMapping("/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivityStats() {
        Map<String, Object> activityStats = new HashMap<>();
        
        long totalUsers = userService.getUserCount();
        
        // 获取所有用户并计算每个用户的实验数量
        List<User> allUsers = userService.getAllUsers();
        Map<String, Integer> userExperimentCounts = new HashMap<>();
        
        for (User user : allUsers) {
            List<ExperimentStatisticsDto> userExperiments = experimentStatisticsService.getUserStatistics(user.getId());
            userExperimentCounts.put(user.getName(), userExperiments.size());
        }
        
        activityStats.put("totalUsers", totalUsers);
        activityStats.put("userExperimentCounts", userExperimentCounts);
        
        return ResponseEntity.ok(activityStats);
    }

    // 获取指定用户的所有统计信息
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserComprehensiveStats(@PathVariable Long userId) {
        Map<String, Object> userStats = new HashMap<>();
        
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<ExperimentStatisticsDto> userExperiments = experimentStatisticsService.getUserStatistics(userId);
        
        userStats.put("user", user);
        userStats.put("experiments", userExperiments);
        userStats.put("experimentCount", userExperiments.size());
        
        // 计算该用户的平均实验数据
        if (!userExperiments.isEmpty()) {
            double avgDataPoints = userExperiments.stream()
                    .mapToInt(ExperimentStatisticsDto::getTotalDataPoints)
                    .average()
                    .orElse(0.0);
            userStats.put("averageDataPointsPerExperiment", avgDataPoints);
            
            double avgConcentration = userExperiments.stream()
                    .mapToDouble(ExperimentStatisticsDto::getAverageConcentration)
                    .average()
                    .orElse(0.0);
            userStats.put("averageConcentration", avgConcentration);
        }
        
        return ResponseEntity.ok(userStats);
    }
}