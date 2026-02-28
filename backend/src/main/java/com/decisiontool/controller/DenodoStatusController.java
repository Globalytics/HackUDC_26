package com.decisiontool.controller;

import com.decisiontool.service.DenodoApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/denodo")
public class DenodoStatusController {

    private final DenodoApiClient denodoApiClient;

    public DenodoStatusController(DenodoApiClient denodoApiClient) {
        this.denodoApiClient = denodoApiClient;
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        String info = denodoApiClient.health().block();
        return ResponseEntity.ok(info);
    }
}