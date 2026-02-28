package com.decisiontool.controller;

import com.decisiontool.dto.DatasetDetailDTO;
import com.decisiontool.dto.DatasetSummaryDTO;
import com.decisiontool.dto.UploadDatasetRequest;
import com.decisiontool.service.DataService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private static final Logger log = LoggerFactory.getLogger(DatasetController.class);

    private final DataService dataService;

    public DatasetController(DataService dataService) {
        this.dataService = dataService;
    }

    /** GET /api/datasets → listado resumido */
    @GetMapping
    public ResponseEntity<List<DatasetSummaryDTO>> list() {
        return ResponseEntity.ok(dataService.listAll());
    }

    /** GET /api/datasets/{id} → detalle completo con filas y estadísticas */
    @GetMapping("/{id}")
    public ResponseEntity<DatasetDetailDTO> detail(@PathVariable String id) {
        try {
            return ResponseEntity.ok(dataService.getDetail(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/datasets → subir dataset en CSV o JSON */
    @PostMapping
    public ResponseEntity<DatasetSummaryDTO> upload(@Valid @RequestBody UploadDatasetRequest request) {
        log.info("POST /api/datasets - nombre: '{}'", request.getName());
        DatasetSummaryDTO summary = dataService.upload(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(summary);
    }

    /** DELETE /api/datasets/{id} → eliminar dataset */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            dataService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
