package com.swole.platform.dto;

import java.time.LocalDateTime;

public class ExperimentStatisticsDto {
    private Long experimentId;
    private String experimentType;
    private String userName;
    private LocalDateTime experimentTime;
    private int totalDataPoints;
    private double averageConcentration;
    private double confidenceLevel;
    private String analysisSummary;

    // 构造函数
    public ExperimentStatisticsDto() {}

    public ExperimentStatisticsDto(Long experimentId, String experimentType, String userName, 
                                  LocalDateTime experimentTime, int totalDataPoints, 
                                  double averageConcentration, double confidenceLevel, String analysisSummary) {
        this.experimentId = experimentId;
        this.experimentType = experimentType;
        this.userName = userName;
        this.experimentTime = experimentTime;
        this.totalDataPoints = totalDataPoints;
        this.averageConcentration = averageConcentration;
        this.confidenceLevel = confidenceLevel;
        this.analysisSummary = analysisSummary;
    }

    // Getter和Setter方法
    public Long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }

    public String getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getExperimentTime() {
        return experimentTime;
    }

    public void setExperimentTime(LocalDateTime experimentTime) {
        this.experimentTime = experimentTime;
    }

    public int getTotalDataPoints() {
        return totalDataPoints;
    }

    public void setTotalDataPoints(int totalDataPoints) {
        this.totalDataPoints = totalDataPoints;
    }

    public double getAverageConcentration() {
        return averageConcentration;
    }

    public void setAverageConcentration(double averageConcentration) {
        this.averageConcentration = averageConcentration;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getAnalysisSummary() {
        return analysisSummary;
    }

    public void setAnalysisSummary(String analysisSummary) {
        this.analysisSummary = analysisSummary;
    }
}