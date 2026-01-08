package com.swole.platform.repository;

import com.swole.platform.model.entity.TargetDetectionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TargetDetectionDataRepository extends JpaRepository<TargetDetectionData, Long> {
    List<TargetDetectionData> findByExperimentId(Long experimentId);
    List<TargetDetectionData> findByGroupNumber(Integer groupNumber);
}