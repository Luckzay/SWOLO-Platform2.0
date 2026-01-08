package com.swole.platform.controller.v1;

import com.swole.platform.model.entity.Experiment;
import com.swole.platform.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/experiments")
public class ExperimentController {

    @Autowired
    private ExperimentService experimentService;

    @GetMapping
    public List<Experiment> getAllExperiments() {
        return experimentService.getAllExperiments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Experiment> getExperimentById(@PathVariable Long id) {
        Optional<Experiment> experiment = experimentService.getExperimentById(id);
        if (experiment.isPresent()) {
            return ResponseEntity.ok(experiment.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Experiment createExperiment(@RequestBody Experiment experiment) {
        return experimentService.createExperiment(experiment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Experiment> updateExperiment(@PathVariable Long id, @RequestBody Experiment experiment) {
        try {
            Experiment updatedExperiment = experimentService.updateExperiment(id, experiment);
            return ResponseEntity.ok(updatedExperiment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperiment(@PathVariable Long id) {
        try {
            experimentService.deleteExperiment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public List<Experiment> getExperimentsByUserId(@PathVariable Long userId) {
        return experimentService.getExperimentsByUserId(userId);
    }

    @GetMapping("/type/{experimentTypeId}")
    public List<Experiment> getExperimentsByExperimentTypeId(@PathVariable Long experimentTypeId) {
        return experimentService.getExperimentsByExperimentTypeId(experimentTypeId);
    }
}