package com.swole.platform.repository;

import com.swole.platform.model.entity.ExperimentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperimentTypeRepository extends JpaRepository<ExperimentType, Long> {
    ExperimentType findByTypeName(String typeName);
}