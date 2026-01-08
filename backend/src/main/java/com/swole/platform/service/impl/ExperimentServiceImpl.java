package com.swole.platform.service.impl;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.Experiment;
import com.swole.platform.repository.ExperimentRepository;
import com.swole.platform.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private ExperimentRepository experimentRepository;

    @Override
    public List<Experiment> getAllExperiments() {
        return experimentRepository.findAll();
    }

    @Override
    public Optional<Experiment> getExperimentById(Long id) {
        return experimentRepository.findById(id);
    }

    @Override
    public Experiment createExperiment(Experiment experiment) {
        return experimentRepository.save(experiment);
    }

    @Override
    public Experiment updateExperiment(Long id, Experiment experiment) {
        Experiment existingExperiment = experimentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Experiment", "id", id));
        
        experiment.setId(id);
        return experimentRepository.save(experiment);
    }

    @Override
    public void deleteExperiment(Long id) {
        Experiment existingExperiment = experimentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Experiment", "id", id));
        
        experimentRepository.deleteById(id);
    }

    @Override
    public List<Experiment> getExperimentsByUserId(Long userId) {
        return experimentRepository.findByUserId(userId);
    }

    @Override
    public List<Experiment> getExperimentsByExperimentTypeId(Long experimentTypeId) {
        return experimentRepository.findByExperimentTypeId(experimentTypeId);
    }
}