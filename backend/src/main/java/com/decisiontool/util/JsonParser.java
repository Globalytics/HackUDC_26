package com.decisiontool.util;

import com.decisiontool.model.DatasetColumn;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JsonParser {

    private final TypeInferrer typeInferrer;
    private final ObjectMapper objectMapper;

    public JsonParser(TypeInferrer typeInferrer, ObjectMapper objectMapper) {
        this.typeInferrer = typeInferrer;
        this.objectMapper = objectMapper;
    }

    /** Parsea un JSON array y devuelve las filas como lista de mapas */
    public List<Map<String, Object>> parseRows(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent,
                    new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear JSON: " + e.getMessage(), e);
        }
    }

    /** Infiere las columnas con su tipo a partir del contenido JSON */
    public List<DatasetColumn> parseColumns(String jsonContent) {
        List<Map<String, Object>> rows = parseRows(jsonContent);
        if (rows.isEmpty()) return List.of();

        Set<String> keys = rows.get(0).keySet();
        int sampleSize = Math.min(rows.size(), 20);

        List<DatasetColumn> columns = new ArrayList<>();
        for (String key : keys) {
            List<String> sample = rows.stream()
                    .limit(sampleSize)
                    .map(r -> r.get(key) != null ? r.get(key).toString() : "")
                    .collect(Collectors.toList());

            String type = typeInferrer.inferType(sample);
            columns.add(DatasetColumn.builder()
                    .name(key)
                    .type(type)
                    .numeric("NUMBER".equals(type))
                    .nullable(sample.stream().anyMatch(String::isBlank))
                    .build());
        }
        return columns;
    }
}
