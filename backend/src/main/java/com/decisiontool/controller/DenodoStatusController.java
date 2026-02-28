package com.decisiontool.controller;

import com.decisiontool.dto.DenodoGetMetadataResponse;
import com.decisiontool.service.DenodoApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Endpoint para refrescar metadata manualmente (botón Refresh)
     */
    @PostMapping("/refresh-metadata")
    public ResponseEntity<String> refreshMetadata(
            @RequestParam("dbs") String dbsCsv
    ) {
        return denodoApiClient.getMetadataRaw(dbsCsv).block();
    }
}