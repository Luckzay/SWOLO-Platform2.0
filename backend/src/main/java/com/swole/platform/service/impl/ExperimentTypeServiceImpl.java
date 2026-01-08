package com.swole.platform.service.impl;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.ExperimentType;
import com.swole.platform.repository.ExperimentTypeRepository;
import com.swole.platform.service.ExperimentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ExperimentTypeServiceImpl implements ExperimentTypeService {

    @Autowired
    private ExperimentTypeRepository experimentTypeRepository;

    @Override
    public List<ExperimentType> getAllExperimentTypes() {
        return experimentTypeRepository.findAll();
    }

    @Override
    public Optional<ExperimentType> getExperimentTypeById(Long id) {
        return experimentTypeRepository.findById(id);
    }

    @Override
    public ExperimentType createExperimentType(ExperimentType experimentType) {
        return experimentTypeRepository.save(experimentType);
    }

    @Override
    public ExperimentType updateExperimentType(Long id, ExperimentType experimentType) {
        ExperimentType existingExperimentType = experimentTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ExperimentType", "id", id));
        
        experimentType.setId(id);
        return experimentTypeRepository.save(experimentType);
    }

    @Override
    public void deleteExperimentType(Long id) {
        ExperimentType existingExperimentType = experimentTypeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ExperimentType", "id", id));
        
        experimentTypeRepository.deleteById(id);
    }

    @Override
    public ExperimentType findByTypeName(String typeName) {
        return experimentTypeRepository.findByTypeName(typeName);
    }
}