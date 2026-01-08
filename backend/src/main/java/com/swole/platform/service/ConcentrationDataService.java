package com.swole.platform.service;

import com.swole.platform.model.entity.ConcentrationData;
import java.util.List;
import java.util.Optional;

public interface ConcentrationDataService {
    List<ConcentrationData> getAllConcentrationData();
    Optional<ConcentrationData> getConcentrationDataById(Long id);
    ConcentrationData createConcentrationData(ConcentrationData concentrationData);
    ConcentrationData updateConcentrationData(Long id, ConcentrationData concentrationData);
    void deleteConcentrationData(Long id);
    List<ConcentrationData> getConcentrationDataByExperimentId(Long experimentId);
    List<ConcentrationData> getConcentrationDataByGroupNumber(Integer groupNumber);
}