package com.decisiontool.controller;

import com.decisiontool.dto.DatasetDetailDTO;
import com.decisiontool.dto.DatasetSummaryDTO;
import com.decisiontool.service.DataService;
import com.decisiontool.service.DenodoAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DataService dataService;
    private final DenodoAccessService denodoAccessService;

    public DatasetController(DataService dataService, DenodoAccessService denodoAccessService) {
        this.dataService = dataService;
        this.denodoAccessService = denodoAccessService;
    }

    /**
     * GET /api/datasets
     * Para el dropdown del chatbot: lista BDs accesibles vía Denodo AI SDK.
     */
    @GetMapping
    public ResponseEntity<List<DatasetSummaryDTO>> listAccessibleDatabases() {
        List<String> dbs = denodoAccessService.listAccessibleDatabases();

        List<DatasetSummaryDTO> result = dbs.stream()
                .map(db -> DatasetSummaryDTO.builder()
                        .id(db)
                        .name(db)
                        .description("")
                        .source("denodo-ai-sdk")
                        .rowCount(0)
                        .columnCount(0)
                        .createdAt(null)
                        .build()
                )
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/datasets/uploaded
     * Mantiene vuestro listado antiguo (datasets subidos/gestionados por vuestra app).
     */
    @GetMapping("/uploaded")
    public ResponseEntity<List<DatasetSummaryDTO>> listUploaded() {
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