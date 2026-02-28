package com.decisiontool.service;

import com.decisiontool.dto.DecisionRequest;
import com.decisiontool.dto.DecisionResponse;
import com.decisiontool.model.Dataset;
import com.decisiontool.util.StatsCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);

    private final DataService dataService;
    private final StatsCalculator statsCalculator;

    public DecisionService(DataService dataService, StatsCalculator statsCalculator) {
        this.dataService     = dataService;
        this.statsCalculator = statsCalculator;
    }

    // ── Punto de entrada ──────────────────────────────────────────────────────

    public DecisionResponse analyze(DecisionRequest request) {
        log.info("Analizando decisión: '{}'", request.getProblem());

        try {
            // 1. Obtener el dataset si se indicó uno
            List<Map<String, Object>> rows = resolveRows(request.getDatasetId());

            if (rows.isEmpty()) {
                return DecisionResponse.builder()
                        .error(true)
                        .errorMessage("No hay datos disponibles para tomar una decisión.")
                        .build();
            }

            // 2. Calcular métricas
            Map<String, Object> metrics = computeMetrics(rows);

            // 3. Comparar alternativas (columna de texto más representativa)
            String labelCol   = detectLabelColumn(rows);
            String metricCol  = detectBestNumericColumn(rows);
            List<Map<String, Object>> ranked = rankAlternatives(rows, metricCol);

            // 4. Generar recomendación
            String recommendation = buildRecommendation(ranked, labelCol, metricCol, request.getProblem());
            String justification  = buildJustification(ranked, labelCol, metricCol, metrics);

            return DecisionResponse.builder()
                    .recommendation(recommendation)
                    .justification(justification)
                    .metrics(metrics)
                    .alternatives(ranked)
                    .error(false)
                    .build();

        } catch (Exception ex) {
            log.error("Error en el motor de decisión", ex);
            return DecisionResponse.builder()
                    .error(true)
                    .errorMessage("Error al procesar la decisión: " + ex.getMessage())
                    .build();
        }
    }

    // ── Métricas ──────────────────────────────────────────────────────────────

    private Map<String, Object> computeMetrics(List<Map<String, Object>> rows) {
        Map<String, Object> metrics = new LinkedHashMap<>();
        var stats = statsCalculator.calculate(rows);

        metrics.put("totalRows",  stats.getRowCount());
        metrics.put("totalCols",  stats.getColumnCount());
        metrics.put("means",      stats.getMeans());
        metrics.put("mins",       stats.getMins());
        metrics.put("maxs",       stats.getMaxs());
        metrics.put("stdDevs",    stats.getStdDevs());

        // Crecimiento: si hay al menos 2 filas y una columna numérica, calcular tendencia
        if (rows.size() >= 2) {
            String numCol = detectBestNumericColumn(rows);
            if (numCol != null) {
                double first = statsCalculator.toDouble(rows.get(0).get(numCol));
                double last  = statsCalculator.toDouble(rows.get(rows.size() - 1).get(numCol));
                metrics.put("growthRate_" + numCol, statsCalculator.growthRate(first, last));
            }
        }
        return metrics;
    }

    // ── Ranking de alternativas ───────────────────────────────────────────────

    private List<Map<String, Object>> rankAlternatives(List<Map<String, Object>> rows, String metricCol) {
        if (metricCol == null) return rows;
        List<Map<String, Object>> ranked = statsCalculator.rankBy(rows, metricCol);
        // Añadir posición de ranking a cada fila
        for (int i = 0; i < ranked.size(); i++) {
            Map<String, Object> row = new LinkedHashMap<>(ranked.get(i));
            row.put("_rank", i + 1);
            ranked.set(i, row);
        }
        return ranked;
    }

    // ── Generación de texto ───────────────────────────────────────────────────

    private String buildRecommendation(List<Map<String, Object>> ranked,
                                       String labelCol,
                                       String metricCol,
                                       String problem) {
        if (ranked.isEmpty()) return "No hay suficientes datos para generar una recomendación.";

        Map<String, Object> best = ranked.get(0);
        String bestLabel  = labelCol  != null ? String.valueOf(best.get(labelCol))  : "la opción #1";
        String bestMetric = metricCol != null ? String.valueOf(best.get(metricCol)) : "N/A";

        return String.format(
            "Para el problema **\"%s\"**, la mejor opción es **%s** " +
            "con un valor de `%s` en `%s`, que la sitúa en el **puesto #1** del ranking.",
            problem, bestLabel, bestMetric, metricCol != null ? metricCol : "métrica principal"
        );
    }

    private String buildJustification(List<Map<String, Object>> ranked,
                                      String labelCol,
                                      String metricCol,
                                      Map<String, Object> metrics) {
        StringBuilder sb = new StringBuilder();
        sb.append("### Justificación basada en datos\n\n");

        // Top 3
        sb.append("**Top 3 alternativas");
        if (metricCol != null) sb.append(" por `").append(metricCol).append("`");
        sb.append(":**\n\n");

        ranked.stream().limit(3).forEach(row -> {
            String label  = labelCol  != null ? String.valueOf(row.get(labelCol))  : "Opción";
            String metric = metricCol != null ? String.valueOf(row.get(metricCol)) : "";
            int rank = row.containsKey("_rank") ? (int) row.get("_rank") : 0;
            sb.append(String.format("- **#%d %s**: %s%n", rank, label,
                    metricCol != null ? "`" + metricCol + "` = " + metric : ""));
        });

        // Estadísticas resumen
        sb.append("\n**Estadísticas del dataset:**\n");
        sb.append("- Total de registros analizados: `").append(metrics.get("totalRows")).append("`\n");

        @SuppressWarnings("unchecked")
        Map<String, Double> means = (Map<String, Double>) metrics.get("means");
        if (means != null && !means.isEmpty()) {
            sb.append("- Medias: ");
            means.forEach((col, val) -> sb.append("`").append(col).append("` = ").append(val).append("  "));
            sb.append("\n");
        }

        return sb.toString();
    }

    // ── Detección de columnas ─────────────────────────────────────────────────

    private String detectLabelColumn(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return null;
        // La primera columna de tipo texto suele ser la etiqueta
        return rows.get(0).entrySet().stream()
                .filter(e -> e.getValue() instanceof String)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private String detectBestNumericColumn(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return null;
        // Preferir columnas cuyo nombre sugiera una métrica de negocio
        List<String> preferred = List.of("ventas", "sales", "revenue", "ingresos",
                "valor", "value", "score", "total", "cantidad", "amount");
        return rows.get(0).entrySet().stream()
                .filter(e -> e.getValue() instanceof Number)
                .map(Map.Entry::getKey)
                .min(Comparator.comparingInt(col -> {
                    String lower = col.toLowerCase();
                    int idx = preferred.indexOf(lower);
                    return idx == -1 ? Integer.MAX_VALUE : idx;
                }))
                .orElse(null);
    }

    private List<Map<String, Object>> resolveRows(String datasetId) {
        if (datasetId == null) return List.of();
        return dataService.findById(datasetId)
                .map(Dataset::getRows)
                .orElse(List.of());
    }
}

