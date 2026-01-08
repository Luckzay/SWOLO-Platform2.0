package com.swole.platform.controller.v1;

import com.swole.platform.model.entity.*;
import com.swole.platform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/experiment-data")
public class ExperimentDataController {

    @Autowired
    private TargetDetectionDataService targetDetectionDataService;

    @Autowired
    private ConcentrationDataService concentrationDataService;

    @Autowired
    private GeneralDataService generalDataService;

    // Target Detection Data endpoints
    @GetMapping("/target-detection")
    public List<TargetDetectionData> getAllTargetDetectionData() {
        return targetDetectionDataService.getAllTargetDetectionData();
    }

    @GetMapping("/target-detection/{id}")
    public ResponseEntity<TargetDetectionData> getTargetDetectionDataById(@PathVariable Long id) {
        Optional<TargetDetectionData> data = targetDetectionDataService.getTargetDetectionDataById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/target-detection")
    public TargetDetectionData createTargetDetectionData(@RequestBody TargetDetectionData targetDetectionData) {
        return targetDetectionDataService.createTargetDetectionData(targetDetectionData);
    }

    @PutMapping("/target-detection/{id}")
    public ResponseEntity<TargetDetectionData> updateTargetDetectionData(@PathVariable Long id, @RequestBody TargetDetectionData targetDetectionData) {
        try {
            TargetDetectionData updatedData = targetDetectionDataService.updateTargetDetectionData(id, targetDetectionData);
            return ResponseEntity.ok(updatedData);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/target-detection/{id}")
    public ResponseEntity<Void> deleteTargetDetectionData(@PathVariable Long id) {
        try {
            targetDetectionDataService.deleteTargetDetectionData(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/target-detection/experiment/{experimentId}")
    public List<TargetDetectionData> getTargetDetectionDataByExperimentId(@PathVariable Long experimentId) {
        return targetDetectionDataService.getTargetDetectionDataByExperimentId(experimentId);
    }

    @GetMapping("/target-detection/group/{groupNumber}")
    public List<TargetDetectionData> getTargetDetectionDataByGroupNumber(@PathVariable Integer groupNumber) {
        return targetDetectionDataService.getTargetDetectionDataByGroupNumber(groupNumber);
    }

    // Concentration Data endpoints
    @GetMapping("/concentration")
    public List<ConcentrationData> getAllConcentrationData() {
        return concentrationDataService.getAllConcentrationData();
    }

    @GetMapping("/concentration/{id}")
    public ResponseEntity<ConcentrationData> getConcentrationDataById(@PathVariable Long id) {
        Optional<ConcentrationData> data = concentrationDataService.getConcentrationDataById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/concentration")
    public ConcentrationData createConcentrationData(@RequestBody ConcentrationData concentrationData) {
        return concentrationDataService.createConcentrationData(concentrationData);
    }

    @PutMapping("/concentration/{id}")
    public ResponseEntity<ConcentrationData> updateConcentrationData(@PathVariable Long id, @RequestBody ConcentrationData concentrationData) {
        try {
            ConcentrationData updatedData = concentrationDataService.updateConcentrationData(id, concentrationData);
            return ResponseEntity.ok(updatedData);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/concentration/{id}")
    public ResponseEntity<Void> deleteConcentrationData(@PathVariable Long id) {
        try {
            concentrationDataService.deleteConcentrationData(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/concentration/experiment/{experimentId}")
    public List<ConcentrationData> getConcentrationDataByExperimentId(@PathVariable Long experimentId) {
        return concentrationDataService.getConcentrationDataByExperimentId(experimentId);
    }

    @GetMapping("/concentration/group/{groupNumber}")
    public List<ConcentrationData> getConcentrationDataByGroupNumber(@PathVariable Integer groupNumber) {
        return concentrationDataService.getConcentrationDataByGroupNumber(groupNumber);
    }

    // General Data endpoints
    @GetMapping("/general")
    public List<GeneralData> getAllGeneralData() {
        return generalDataService.getAllGeneralData();
    }

    @GetMapping("/general/{id}")
    public ResponseEntity<GeneralData> getGeneralDataById(@PathVariable Long id) {
        Optional<GeneralData> data = generalDataService.getGeneralDataById(id);
        if (data.isPresent()) {
            return ResponseEntity.ok(data.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/general")
    public GeneralData createGeneralData(@RequestBody GeneralData generalData) {
        return generalDataService.createGeneralData(generalData);
    }

    @PutMapping("/general/{id}")
    public ResponseEntity<GeneralData> updateGeneralData(@PathVariable Long id, @RequestBody GeneralData generalData) {
        try {
            GeneralData updatedData = generalDataService.updateGeneralData(id, generalData);
            return ResponseEntity.ok(updatedData);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/general/{id}")
    public ResponseEntity<Void> deleteGeneralData(@PathVariable Long id) {
        try {
            generalDataService.deleteGeneralData(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/general/experiment/{experimentId}")
    public List<GeneralData> getGeneralDataByExperimentId(@PathVariable Long experimentId) {
        return generalDataService.getGeneralDataByExperimentId(experimentId);
    }

    @GetMapping("/general/group/{groupNumber}")
    public List<GeneralData> getGeneralDataByGroupNumber(@PathVariable Integer groupNumber) {
        return generalDataService.getGeneralDataByGroupNumber(groupNumber);
    }
}