package com.swole.platform.repository;

import com.swole.platform.model.entity.ConcentrationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConcentrationDataRepository extends JpaRepository<ConcentrationData, Long> {
    List<ConcentrationData> findByExperimentId(Long experimentId);
    List<ConcentrationData> findByGroupNumber(Integer groupNumber);
}