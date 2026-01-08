package com.swole.platform.repository;

import com.swole.platform.model.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {
    List<Experiment> findByUserId(Long userId);
    List<Experiment> findByExperimentTypeId(Long experimentTypeId);
}