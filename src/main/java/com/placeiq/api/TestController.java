package com.placeiq.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(value = "/", produces = "application/json")
    public String healthCheck() {
        return "\"API Running\"";
    }
}
