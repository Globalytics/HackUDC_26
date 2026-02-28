package com.decisiontool.util;

import com.decisiontool.model.DatasetStats;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StatsCalculator {

    /** Calcula estadísticas completas sobre las filas de un dataset */
    public DatasetStats calculate(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return DatasetStats.builder()
                    .rowCount(0).columnCount(0)
                    .means(Map.of()).mins(Map.of()).maxs(Map.of()).stdDevs(Map.of())
                    .build();
        }

        Set<String> numericCols = detectNumericColumns(rows);

        Map<String, Double> means  = new HashMap<>();
        Map<String, Double> mins   = new HashMap<>();
        Map<String, Double> maxs   = new HashMap<>();
        Map<String, Double> stdDevs = new HashMap<>();

        for (String col : numericCols) {
            List<Double> values = extractDoubles(rows, col);
            if (values.isEmpty()) continue;

            double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double min  = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max  = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double std  = stdDev(values, mean);

            means.put(col, round(mean));
            mins.put(col, round(min));
            maxs.put(col, round(max));
            stdDevs.put(col, round(std));
        }

        return DatasetStats.builder()
                .rowCount(rows.size())
                .columnCount(rows.get(0).size())
                .means(means)
                .mins(mins)
                .maxs(maxs)
                .stdDevs(stdDevs)
                .build();
    }

    /** Devuelve el ranking de filas por una columna numérica (mayor = rank 1) */
    public List<Map<String, Object>> rankBy(List<Map<String, Object>> rows, String column) {
        return rows.stream()
                .filter(r -> r.get(column) != null)
                .sorted((a, b) -> {
                    double va = toDouble(a.get(column));
                    double vb = toDouble(b.get(column));
                    return Double.compare(vb, va); // descendente
                })
                .collect(Collectors.toList());
    }

    /** Calcula el crecimiento porcentual entre dos valores */
    public double growthRate(double from, double to) {
        if (from == 0) return 0;
        return round(((to - from) / Math.abs(from)) * 100);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Set<String> detectNumericColumns(List<Map<String, Object>> rows) {
        return rows.get(0).keySet().stream()
                .filter(col -> rows.stream()
                        .anyMatch(r -> r.get(col) instanceof Number))
                .collect(Collectors.toSet());
    }

    private List<Double> extractDoubles(List<Map<String, Object>> rows, String col) {
        return rows.stream()
                .map(r -> r.get(col))
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).doubleValue())
                .collect(Collectors.toList());
    }

    private double stdDev(List<Double> values, double mean) {
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    public double toDouble(Object value) {
        if (value instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(value.toString()); }
        catch (Exception e) { return 0; }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
