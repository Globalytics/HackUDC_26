package com.decisiontool.controller;

import com.decisiontool.dto.DecisionRequest;
import com.decisiontool.dto.DecisionResponse;
import com.decisiontool.service.DecisionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decision")
public class DecisionController {

    private static final Logger log = LoggerFactory.getLogger(DecisionController.class);

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    /**
     * Analiza un dataset y devuelve una recomendación razonada.
     * POST /api/decision
     */
    @PostMapping
    public ResponseEntity<DecisionResponse> analyze(@Valid @RequestBody DecisionRequest request) {
        log.info("POST /api/decision - problema: '{}'", request.getProblem());
        DecisionResponse response = decisionService.analyze(request);

        if (response.isError()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}

