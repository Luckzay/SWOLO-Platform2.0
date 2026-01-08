package com.swole.platform.service.statistics.impl;

import com.swole.platform.dto.ExperimentStatisticsDto;
import com.swole.platform.model.entity.*;
import com.swole.platform.repository.*;
import com.swole.platform.service.statistics.ExperimentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExperimentStatisticsServiceImpl implements ExperimentStatisticsService {

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private TargetDetectionDataRepository targetDetectionDataRepository;

    @Autowired
    private ConcentrationDataRepository concentrationDataRepository;

    @Autowired
    private GeneralDataRepository generalDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExperimentTypeRepository experimentTypeRepository;

    @Override
    public List<ExperimentStatisticsDto> getExperimentStatistics(Long experimentId) {
        List<ExperimentStatisticsDto> stats = new ArrayList<>();

        Optional<Experiment> experimentOpt = experimentRepository.findById(experimentId);
        if (experimentOpt.isPresent()) {
            Experiment experiment = experimentOpt.get();

            // 计算统计数据
            int totalDataPoints = 0;
            double avgConcentration = 0.0;
            double confidenceLevel = 0.0;

            // 计算目标检测数据点数
            int targetDetectionCount = targetDetectionDataRepository.findByExperimentId(experimentId).size();
            totalDataPoints += targetDetectionCount;

            // 计算浓度数据点数和平均浓度
            List<ConcentrationData> concentrationDataList = concentrationDataRepository.findByExperimentId(experimentId);
            int concentrationCount = concentrationDataList.size();
            totalDataPoints += concentrationCount;

            if (!concentrationDataList.isEmpty()) {
                avgConcentration = concentrationDataList.stream()
                        .mapToDouble(ConcentrationData::getConcentration)
                        .average()
                        .orElse(0.0);
            }

            // 计算通用数据点数
            int generalDataCount = generalDataRepository.findByExperimentId(experimentId).size();
            totalDataPoints += generalDataCount;

            // 计算平均置信度
            List<Double> allConfidences = new ArrayList<>();
            concentrationDataList.forEach(cd -> allConfidences.add(cd.getConfidence()));
            targetDetectionDataRepository.findByExperimentId(experimentId)
                    .forEach(td -> allConfidences.add(td.getConfidence()));
            if (!allConfidences.isEmpty()) {
                confidenceLevel = allConfidences.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
            }

            // 获取用户和实验类型信息
            String userName = "Unknown";
            String experimentTypeName = "Unknown";
            Optional<User> userOpt = userRepository.findById(experiment.getUserId());
            Optional<ExperimentType> typeOpt = experimentTypeRepository.findById(experiment.getExperimentTypeId());

            if (userOpt.isPresent()) {
                userName = userOpt.get().getName();
            }
            if (typeOpt.isPresent()) {
                experimentTypeName = typeOpt.get().getTypeName();
            }

            ExperimentStatisticsDto stat = new ExperimentStatisticsDto(
                    experimentId,
                    experimentTypeName,
                    userName,
                    experiment.getExperimentTime(),
                    totalDataPoints,
                    avgConcentration,
                    confidenceLevel,
                    String.format("实验包含%d个数据点，平均浓度%.2f，平均置信度%.2f", 
                                 totalDataPoints, avgConcentration, confidenceLevel)
            );
            stats.add(stat);
        }

        return stats;
    }

    @Override
    public List<ExperimentStatisticsDto> getUserExperimentStatistics(Long userId) {
        List<Experiment> experiments = experimentRepository.findByUserId(userId);
        List<ExperimentStatisticsDto> stats = new ArrayList<>();

        for (Experiment experiment : experiments) {
            stats.addAll(getExperimentStatistics(experiment.getId()));
        }

        return stats;
    }

    @Override
    public List<ExperimentStatisticsDto> getExperimentTypeStatistics(Long experimentTypeId) {
        List<Experiment> experiments = experimentRepository.findByExperimentTypeId(experimentTypeId);
        List<ExperimentStatisticsDto> stats = new ArrayList<>();

        for (Experiment experiment : experiments) {
            stats.addAll(getExperimentStatistics(experiment.getId()));
        }

        return stats;
    }

    @Override
    public List<ExperimentStatisticsDto> getTimeRangeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<Experiment> experiments = experimentRepository.findAll()
                .stream()
                .filter(exp -> exp.getExperimentTime().isAfter(startTime) && exp.getExperimentTime().isBefore(endTime))
                .collect(Collectors.toList());

        List<ExperimentStatisticsDto> stats = new ArrayList<>();
        for (Experiment experiment : experiments) {
            stats.addAll(getExperimentStatistics(experiment.getId()));
        }

        return stats;
    }

    @Override
    public ExperimentStatisticsDto getOverallStatistics() {
        List<Experiment> allExperiments = experimentRepository.findAll();
        int totalExperiments = allExperiments.size();
        int totalDataPoints = 0;
        double totalConcentration = 0.0;
        int concentrationCount = 0;

        for (Experiment exp : allExperiments) {
            List<ConcentrationData> concData = concentrationDataRepository.findByExperimentId(exp.getId());
            totalDataPoints += targetDetectionDataRepository.findByExperimentId(exp.getId()).size();
            totalDataPoints += concData.size();
            totalDataPoints += generalDataRepository.findByExperimentId(exp.getId()).size();

            for (ConcentrationData cd : concData) {
                totalConcentration += cd.getConcentration();
                concentrationCount++;
            }
        }

        double avgConcentration = concentrationCount > 0 ? totalConcentration / concentrationCount : 0.0;
        double avgDataPoints = totalExperiments > 0 ? (double) totalDataPoints / totalExperiments : 0.0;

        return new ExperimentStatisticsDto(
                null,
                "Overall",
                "All Users",
                null,
                totalDataPoints,
                avgConcentration,
                0.0, // Placeholder for overall confidence
                String.format("总共%d个实验，%d个数据点，平均%.2f个数据点/实验，平均浓度%.2f", 
                             totalExperiments, totalDataPoints, avgDataPoints, avgConcentration)
        );
    }

    @Override
    public ExperimentStatisticsDto getDetailedAnalysis(Long experimentId) {
        return getExperimentStatistics(experimentId).stream().findFirst().orElse(null);
    }
    
    @Override
    public List<ExperimentStatisticsDto> getUserStatistics(Long userId) {
        List<Experiment> userExperiments = experimentRepository.findByUserId(userId);
        List<ExperimentStatisticsDto> stats = new ArrayList<>();

        for (Experiment experiment : userExperiments) {
            stats.addAll(getExperimentStatistics(experiment.getId()));
        }

        return stats;
    }
}