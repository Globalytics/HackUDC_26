package com.decisiontool.util;

import com.decisiontool.model.DatasetColumn;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CsvParser {

    private final TypeInferrer typeInferrer;

    public CsvParser(TypeInferrer typeInferrer) {
        this.typeInferrer = typeInferrer;
    }

    /** Parsea un CSV y devuelve las filas como lista de mapas */
    public List<Map<String, Object>> parseRows(String csvContent) {
        try (CSVReader reader = new CSVReader(new StringReader(csvContent))) {
            List<String[]> allRows = reader.readAll();
            if (allRows.size() < 2) return List.of();

            String[] headers = allRows.get(0);
            List<Map<String, Object>> result = new ArrayList<>();

            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                Map<String, Object> map = new LinkedHashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    String val = j < row.length ? row[j].trim() : "";
                    map.put(headers[j].trim(), parseValue(val));
                }
                result.add(map);
            }
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear CSV: " + e.getMessage(), e);
        }
    }

    /** Infiere las columnas con su tipo a partir del contenido CSV */
    public List<DatasetColumn> parseColumns(String csvContent) {
        try (CSVReader reader = new CSVReader(new StringReader(csvContent))) {
            List<String[]> allRows = reader.readAll();
            if (allRows.isEmpty()) return List.of();

            String[] headers = allRows.get(0);
            // Muestra: hasta 20 filas para inferir tipo
            int sampleSize = Math.min(allRows.size() - 1, 20);

            List<DatasetColumn> columns = new ArrayList<>();
            for (int j = 0; j < headers.length; j++) {
                final int col = j;
                List<String> sample = allRows.stream()
                        .skip(1).limit(sampleSize)
                        .map(r -> col < r.length ? r[col].trim() : "")
                        .collect(Collectors.toList());

                String type = typeInferrer.inferType(sample);
                columns.add(DatasetColumn.builder()
                        .name(headers[j].trim())
                        .type(type)
                        .numeric("NUMBER".equals(type))
                        .nullable(sample.stream().anyMatch(String::isBlank))
                        .build());
            }
            return columns;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al inferir columnas del CSV: " + e.getMessage(), e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Object parseValue(String value) {
        if (value == null || value.isBlank()) return null;
        if (typeInferrer.isNumber(value)) {
            return value.contains(".") ? Double.parseDouble(value) : Long.parseLong(value);
        }
        if (typeInferrer.isBoolean(value)) {
            return value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes");
        }
        return value;
    }
}
