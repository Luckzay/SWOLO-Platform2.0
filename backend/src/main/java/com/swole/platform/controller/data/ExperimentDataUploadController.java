package com.swole.platform.controller.data;

import com.swole.platform.model.entity.*;
import com.swole.platform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experiment-data/upload")
public class ExperimentDataUploadController {

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private TargetDetectionDataService targetDetectionDataService;

    @Autowired
    private ConcentrationDataService concentrationDataService;

    @Autowired
    private GeneralDataService generalDataService;

    // 上传实验数据（包含多种类型的数据）
    @PostMapping("/experiment/{experimentId}")
    public ResponseEntity<String> uploadExperimentData(
            @PathVariable Long experimentId,
            @RequestParam(required = false) MultipartFile imageData,
            @RequestParam(required = false) String jsonData) {

        try {
            // 验证实验是否存在
            if (!experimentService.getExperimentById(experimentId).isPresent()) {
                return ResponseEntity.badRequest().body("Experiment not found with ID: " + experimentId);
            }

            // 这里可以根据jsonData解析并保存不同类型的数据
            // 为简单起见，这里只做示例实现
            return ResponseEntity.ok("Experiment data uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload experiment data: " + e.getMessage());
        }
    }

    // 批量上传目标检测数据
    @PostMapping("/target-detection/batch")
    public ResponseEntity<String> batchUploadTargetDetectionData(@RequestBody List<TargetDetectionData> dataList) {
        try {
            for (TargetDetectionData data : dataList) {
                targetDetectionDataService.createTargetDetectionData(data);
            }
            return ResponseEntity.ok("Batch upload completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to batch upload target detection data: " + e.getMessage());
        }
    }

    // 批量上传浓度数据
    @PostMapping("/concentration/batch")
    public ResponseEntity<String> batchUploadConcentrationData(@RequestBody List<ConcentrationData> dataList) {
        try {
            for (ConcentrationData data : dataList) {
                concentrationDataService.createConcentrationData(data);
            }
            return ResponseEntity.ok("Batch upload completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to batch upload concentration data: " + e.getMessage());
        }
    }

    // 批量上传通用数据
    @PostMapping("/general/batch")
    public ResponseEntity<String> batchUploadGeneralData(@RequestBody List<GeneralData> dataList) {
        try {
            for (GeneralData data : dataList) {
                generalDataService.createGeneralData(data);
            }
            return ResponseEntity.ok("Batch upload completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to batch upload general data: " + e.getMessage());
        }
    }
}