package com.decisiontool.service;

import com.decisiontool.dto.DatasetDetailDTO;
import com.decisiontool.dto.DatasetSummaryDTO;
import com.decisiontool.dto.UploadDatasetRequest;
import com.decisiontool.model.Dataset;
import com.decisiontool.model.DatasetColumn;
import com.decisiontool.util.CsvParser;
import com.decisiontool.util.JsonParser;
import com.decisiontool.util.StatsCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DataService {

    private static final Logger log = LoggerFactory.getLogger(DataService.class);

    private final CsvParser csvParser;
    private final JsonParser jsonParser;
    private final StatsCalculator statsCalculator;

    // Almacén en memoria: id → Dataset
    private final Map<String, Dataset> store = new ConcurrentHashMap<>();

    public DataService(CsvParser csvParser,
                       JsonParser jsonParser,
                       StatsCalculator statsCalculator) {
        this.csvParser      = csvParser;
        this.jsonParser     = jsonParser;
        this.statsCalculator = statsCalculator;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public List<DatasetSummaryDTO> listAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Dataset::getCreatedAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public Optional<Dataset> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public DatasetDetailDTO getDetail(String id) {
        return findById(id)
                .map(this::toDetail)
                .orElseThrow(() -> new NoSuchElementException("Dataset no encontrado: " + id));
    }

    public DatasetSummaryDTO upload(UploadDatasetRequest request) {
        log.info("Subiendo dataset '{}' con formato '{}'", request.getName(), request.getFormat());

        List<Map<String, Object>> rows;
        List<DatasetColumn> columns;

        if ("json".equalsIgnoreCase(request.getFormat())) {
            rows    = jsonParser.parseRows(request.getContent());
            columns = jsonParser.parseColumns(request.getContent());
        } else {
            // Por defecto CSV
            rows    = csvParser.parseRows(request.getContent());
            columns = csvParser.parseColumns(request.getContent());
        }

        String id = UUID.randomUUID().toString();
        Dataset dataset = Dataset.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .source("upload")
                .columns(columns)
                .rows(rows)
                .stats(statsCalculator.calculate(rows))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        store.put(id, dataset);
        log.info("Dataset '{}' almacenado con id={} ({} filas)", dataset.getName(), id, rows.size());
        return toSummary(dataset);
    }

    public void delete(String id) {
        if (!store.containsKey(id)) {
            throw new NoSuchElementException("Dataset no encontrado: " + id);
        }
        store.remove(id);
        log.info("Dataset {} eliminado", id);
    }

    /** Registra un dataset creado externamente (p.ej. desde DatasetInitializer) */
    public void register(Dataset dataset) {
        store.put(dataset.getId(), dataset);
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private DatasetSummaryDTO toSummary(Dataset d) {
        return DatasetSummaryDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .source(d.getSource())
                .rowCount(d.getStats() != null ? d.getStats().getRowCount() : 0)
                .columnCount(d.getStats() != null ? d.getStats().getColumnCount() : 0)
                .createdAt(d.getCreatedAt())
                .build();
    }

    private DatasetDetailDTO toDetail(Dataset d) {
        return DatasetDetailDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .source(d.getSource())
                .columns(d.getColumns())
                .rows(d.getRows())
                .stats(d.getStats())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
