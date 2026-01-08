package com.swole.platform.repository;

import com.swole.platform.model.entity.GeneralData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GeneralDataRepository extends JpaRepository<GeneralData, Long> {
    List<GeneralData> findByExperimentId(Long experimentId);
    List<GeneralData> findByGroupNumber(Integer groupNumber);
    List<GeneralData> findByDataKey(String dataKey);
}