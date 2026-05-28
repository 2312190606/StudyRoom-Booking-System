package com.example.studyroom.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Value("${spring.application.name:study-room-backend}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String port;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", Instant.now().toString());
        result.put("application", applicationName);
        result.put("port", port);
        return result;
    }
}
