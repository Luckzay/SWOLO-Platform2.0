package com.swole.platform.service;

import com.swole.platform.model.entity.Experiment;
import java.util.List;
import java.util.Optional;

public interface ExperimentService {
    List<Experiment> getAllExperiments();
    Optional<Experiment> getExperimentById(Long id);
    Experiment createExperiment(Experiment experiment);
    Experiment updateExperiment(Long id, Experiment experiment);
    void deleteExperiment(Long id);
    List<Experiment> getExperimentsByUserId(Long userId);
    List<Experiment> getExperimentsByExperimentTypeId(Long experimentTypeId);
}