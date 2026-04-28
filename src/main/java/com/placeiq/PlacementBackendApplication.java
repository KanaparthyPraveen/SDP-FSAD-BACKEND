package com.placeiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlacementBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlacementBackendApplication.class, args);
    }
}
