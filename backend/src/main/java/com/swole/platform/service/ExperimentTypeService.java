package com.swole.platform.service;

import com.swole.platform.model.entity.ExperimentType;
import java.util.List;
import java.util.Optional;

public interface ExperimentTypeService {
    List<ExperimentType> getAllExperimentTypes();
    Optional<ExperimentType> getExperimentTypeById(Long id);
    ExperimentType createExperimentType(ExperimentType experimentType);
    ExperimentType updateExperimentType(Long id, ExperimentType experimentType);
    void deleteExperimentType(Long id);
    ExperimentType findByTypeName(String typeName);
}