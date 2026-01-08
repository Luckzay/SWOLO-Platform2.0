package com.swole.platform.service;

import com.swole.platform.model.entity.GeneralData;
import java.util.List;
import java.util.Optional;

public interface GeneralDataService {
    List<GeneralData> getAllGeneralData();
    Optional<GeneralData> getGeneralDataById(Long id);
    GeneralData createGeneralData(GeneralData generalData);
    GeneralData updateGeneralData(Long id, GeneralData generalData);
    void deleteGeneralData(Long id);
    List<GeneralData> getGeneralDataByExperimentId(Long experimentId);
    List<GeneralData> getGeneralDataByGroupNumber(Integer groupNumber);
    List<GeneralData> getGeneralDataByDataKey(String dataKey);
}