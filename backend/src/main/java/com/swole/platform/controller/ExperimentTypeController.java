package com.swole.platform.controller;

import com.swole.platform.model.entity.ExperimentType;
import com.swole.platform.service.ExperimentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/experiment-types")
public class ExperimentTypeController {

    @Autowired
    private ExperimentTypeService experimentTypeService;

    @GetMapping
    public List<ExperimentType> getAllExperimentTypes() {
        return experimentTypeService.getAllExperimentTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperimentType> getExperimentTypeById(@PathVariable Long id) {
        Optional<ExperimentType> experimentType = experimentTypeService.getExperimentTypeById(id);
        if (experimentType.isPresent()) {
            return ResponseEntity.ok(experimentType.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ExperimentType createExperimentType(@RequestBody ExperimentType experimentType) {
        return experimentTypeService.createExperimentType(experimentType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperimentType> updateExperimentType(@PathVariable Long id, @RequestBody ExperimentType experimentType) {
        try {
            ExperimentType updatedExperimentType = experimentTypeService.updateExperimentType(id, experimentType);
            return ResponseEntity.ok(updatedExperimentType);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperimentType(@PathVariable Long id) {
        try {
            experimentTypeService.deleteExperimentType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{typeName}")
    public ResponseEntity<ExperimentType> getExperimentTypeByName(@PathVariable String typeName) {
        ExperimentType experimentType = experimentTypeService.findByTypeName(typeName);
        if (experimentType != null) {
            return ResponseEntity.ok(experimentType);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}