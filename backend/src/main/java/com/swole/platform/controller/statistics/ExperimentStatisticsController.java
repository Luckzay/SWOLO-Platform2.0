package com.swole.platform.controller.statistics;

import com.swole.platform.dto.ExperimentStatisticsDto;
import com.swole.platform.service.statistics.ExperimentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/statistics/experiments")
public class ExperimentStatisticsController {

    @Autowired
    private ExperimentStatisticsService experimentStatisticsService;

    // 获取特定实验的统计信息
    @GetMapping("/{experimentId}")
    public ResponseEntity<List<ExperimentStatisticsDto>> getExperimentStatistics(@PathVariable Long experimentId) {
        List<ExperimentStatisticsDto> stats = experimentStatisticsService.getExperimentStatistics(experimentId);
        return ResponseEntity.ok(stats);
    }

    // 获取特定用户的实验统计
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExperimentStatisticsDto>> getUserExperimentStatistics(@PathVariable Long userId) {
        List<ExperimentStatisticsDto> stats = experimentStatisticsService.getUserExperimentStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    // 获取特定实验类型的统计
    @GetMapping("/type/{experimentTypeId}")
    public ResponseEntity<List<ExperimentStatisticsDto>> getExperimentTypeStatistics(@PathVariable Long experimentTypeId) {
        List<ExperimentStatisticsDto> stats = experimentStatisticsService.getExperimentTypeStatistics(experimentTypeId);
        return ResponseEntity.ok(stats);
    }

    // 获取时间段内的实验统计
    @GetMapping("/time-range")
    public ResponseEntity<List<ExperimentStatisticsDto>> getTimeRangeStatistics(
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime) {
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        List<ExperimentStatisticsDto> stats = experimentStatisticsService.getTimeRangeStatistics(start, end);
        return ResponseEntity.ok(stats);
    }

    // 获取总体统计摘要
    @GetMapping("/overall")
    public ResponseEntity<ExperimentStatisticsDto> getOverallStatistics() {
        ExperimentStatisticsDto stats = experimentStatisticsService.getOverallStatistics();
        return ResponseEntity.ok(stats);
    }

    // 获取特定实验的详细分析
    @GetMapping("/{experimentId}/detailed")
    public ResponseEntity<ExperimentStatisticsDto> getDetailedAnalysis(@PathVariable Long experimentId) {
        ExperimentStatisticsDto stats = experimentStatisticsService.getDetailedAnalysis(experimentId);
        return ResponseEntity.ok(stats);
    }
}