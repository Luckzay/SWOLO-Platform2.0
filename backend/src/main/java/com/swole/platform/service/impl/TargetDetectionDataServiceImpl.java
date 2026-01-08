package com.swole.platform.service.impl;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.TargetDetectionData;
import com.swole.platform.repository.TargetDetectionDataRepository;
import com.swole.platform.service.TargetDetectionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TargetDetectionDataServiceImpl implements TargetDetectionDataService {

    @Autowired
    private TargetDetectionDataRepository targetDetectionDataRepository;

    @Override
    public List<TargetDetectionData> getAllTargetDetectionData() {
        return targetDetectionDataRepository.findAll();
    }

    @Override
    public Optional<TargetDetectionData> getTargetDetectionDataById(Long id) {
        return targetDetectionDataRepository.findById(id);
    }

    @Override
    public TargetDetectionData createTargetDetectionData(TargetDetectionData targetDetectionData) {
        return targetDetectionDataRepository.save(targetDetectionData);
    }

    @Override
    public TargetDetectionData updateTargetDetectionData(Long id, TargetDetectionData targetDetectionData) {
        TargetDetectionData existingTargetDetectionData = targetDetectionDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TargetDetectionData", "id", id));
        
        targetDetectionData.setId(id);
        return targetDetectionDataRepository.save(targetDetectionData);
    }

    @Override
    public void deleteTargetDetectionData(Long id) {
        TargetDetectionData existingTargetDetectionData = targetDetectionDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TargetDetectionData", "id", id));
        
        targetDetectionDataRepository.deleteById(id);
    }

    @Override
    public List<TargetDetectionData> getTargetDetectionDataByExperimentId(Long experimentId) {
        return targetDetectionDataRepository.findByExperimentId(experimentId);
    }

    @Override
    public List<TargetDetectionData> getTargetDetectionDataByGroupNumber(Integer groupNumber) {
        return targetDetectionDataRepository.findByGroupNumber(groupNumber);
    }
}