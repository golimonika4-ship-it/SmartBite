package com.smartbite.controller;

import com.smartbite.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> checkHealth() {
        HealthResponse response = new HealthResponse(
                "UP",
                "SmartBite Backend API is running smoothly",
                "1.0.0",
                "SmartBite Smart Campus Canteen Management System"
        );
        return ResponseEntity.ok(response);
    }
}
