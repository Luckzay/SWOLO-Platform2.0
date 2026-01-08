package com.swole.platform.service.impl;

import com.swole.platform.exception.ResourceNotFoundException;
import com.swole.platform.model.entity.GeneralData;
import com.swole.platform.repository.GeneralDataRepository;
import com.swole.platform.service.GeneralDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GeneralDataServiceImpl implements GeneralDataService {

    @Autowired
    private GeneralDataRepository generalDataRepository;

    @Override
    public List<GeneralData> getAllGeneralData() {
        return generalDataRepository.findAll();
    }

    @Override
    public Optional<GeneralData> getGeneralDataById(Long id) {
        return generalDataRepository.findById(id);
    }

    @Override
    public GeneralData createGeneralData(GeneralData generalData) {
        return generalDataRepository.save(generalData);
    }

    @Override
    public GeneralData updateGeneralData(Long id, GeneralData generalData) {
        GeneralData existingGeneralData = generalDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GeneralData", "id", id));
        
        generalData.setId(id);
        return generalDataRepository.save(generalData);
    }

    @Override
    public void deleteGeneralData(Long id) {
        GeneralData existingGeneralData = generalDataRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GeneralData", "id", id));
        
        generalDataRepository.deleteById(id);
    }

    @Override
    public List<GeneralData> getGeneralDataByExperimentId(Long experimentId) {
        return generalDataRepository.findByExperimentId(experimentId);
    }

    @Override
    public List<GeneralData> getGeneralDataByGroupNumber(Integer groupNumber) {
        return generalDataRepository.findByGroupNumber(groupNumber);
    }

    @Override
    public List<GeneralData> getGeneralDataByDataKey(String dataKey) {
        return generalDataRepository.findByDataKey(dataKey);
    }
}