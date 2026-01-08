package com.swole.platform.service.impl;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.ConcentrationData;
import com.swole.platform.repository.ConcentrationDataRepository;
import com.swole.platform.service.ConcentrationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ConcentrationDataServiceImpl implements ConcentrationDataService {

    @Autowired
    private ConcentrationDataRepository concentrationDataRepository;

    @Override
    public List<ConcentrationData> getAllConcentrationData() {
        return concentrationDataRepository.findAll();
    }

    @Override
    public Optional<ConcentrationData> getConcentrationDataById(Long id) {
        return concentrationDataRepository.findById(id);
    }

    @Override
    public ConcentrationData createConcentrationData(ConcentrationData concentrationData) {
        return concentrationDataRepository.save(concentrationData);
    }

    @Override
    public ConcentrationData updateConcentrationData(Long id, ConcentrationData concentrationData) {
        ConcentrationData existingConcentrationData = concentrationDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ConcentrationData", "id", id));
        
        concentrationData.setId(id);
        return concentrationDataRepository.save(concentrationData);
    }

    @Override
    public void deleteConcentrationData(Long id) {
        ConcentrationData existingConcentrationData = concentrationDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ConcentrationData", "id", id));
        
        concentrationDataRepository.deleteById(id);
    }

    @Override
    public List<ConcentrationData> getConcentrationDataByExperimentId(Long experimentId) {
        return concentrationDataRepository.findByExperimentId(experimentId);
    }

    @Override
    public List<ConcentrationData> getConcentrationDataByGroupNumber(Integer groupNumber) {
        return concentrationDataRepository.findByGroupNumber(groupNumber);
    }
}