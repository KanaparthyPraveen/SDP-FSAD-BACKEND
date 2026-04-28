package com.placeiq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    private double probability;
    private String reasoning;
    private String recommendation;
}
