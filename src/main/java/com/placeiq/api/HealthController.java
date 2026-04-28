package com.placeiq.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String root() {
        return "API Running";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
