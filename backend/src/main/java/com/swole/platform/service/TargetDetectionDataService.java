package com.swole.platform.service;

import com.swole.platform.model.entity.TargetDetectionData;
import java.util.List;
import java.util.Optional;

public interface TargetDetectionDataService {
    List<TargetDetectionData> getAllTargetDetectionData();
    Optional<TargetDetectionData> getTargetDetectionDataById(Long id);
    TargetDetectionData createTargetDetectionData(TargetDetectionData targetDetectionData);
    TargetDetectionData updateTargetDetectionData(Long id, TargetDetectionData targetDetectionData);
    void deleteTargetDetectionData(Long id);
    List<TargetDetectionData> getTargetDetectionDataByExperimentId(Long experimentId);
    List<TargetDetectionData> getTargetDetectionDataByGroupNumber(Integer groupNumber);
}