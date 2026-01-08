package com.swole.platform.service.statistics;

import com.swole.platform.dto.ExperimentStatisticsDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ExperimentStatisticsService {
    // 获取实验统计信息
    List<ExperimentStatisticsDto> getExperimentStatistics(Long experimentId);
    
    // 获取用户实验统计
    List<ExperimentStatisticsDto> getUserExperimentStatistics(Long userId);
    
    // 获取实验类型统计
    List<ExperimentStatisticsDto> getExperimentTypeStatistics(Long experimentTypeId);
    
    // 获取时间段内的实验统计
    List<ExperimentStatisticsDto> getTimeRangeStatistics(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取总体统计摘要
    ExperimentStatisticsDto getOverallStatistics();
    
    // 获取特定实验的详细分析
    ExperimentStatisticsDto getDetailedAnalysis(Long experimentId);
    
    // 获取用户统计数据
    List<ExperimentStatisticsDto> getUserStatistics(Long userId);
}