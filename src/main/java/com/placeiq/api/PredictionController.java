package com.placeiq.api;

import com.placeiq.dto.ApiResponse;
import com.placeiq.dto.PredictionRequest;
import com.placeiq.dto.PredictionResponse;
import com.placeiq.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PredictionResponse>> predict(@RequestBody PredictionRequest request) {
        PredictionResponse result = predictionService.predict(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
