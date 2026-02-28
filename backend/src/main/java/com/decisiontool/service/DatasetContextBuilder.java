package com.decisiontool.service;

import com.decisiontool.dto.DenodoDataResponse;
import com.decisiontool.dto.DenodoMetadataResponse;
import com.decisiontool.model.Dataset;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DatasetContextBuilder {

    private static final int MAX_ROWS_IN_CONTEXT = 50;

    /**
     * Construye un contexto en texto plano que se enviará junto con la pregunta
     * al motor de decisión / LLM. Combina metadatos y datos reales.
     */
    public String buildContext(DenodoMetadataResponse metadata,
                               DenodoDataResponse data,
                               String question) {
        StringBuilder sb = new StringBuilder();

        sb.append("## PREGUNTA DEL USUARIO\n").append(question).append("\n\n");

        // Sección de metadatos
        if (metadata != null && metadata.isSuccess()) {
            sb.append("## METADATOS DISPONIBLES EN DENODO\n");
            if (!metadata.getRelevantViews().isEmpty()) {
                sb.append("Vistas relevantes: ")
                  .append(String.join(", ", metadata.getRelevantViews()))
                  .append("\n");
            }
            if (!metadata.getColumns().isEmpty()) {
                sb.append("Columnas por vista:\n");
                metadata.getColumns().forEach((view, cols) ->
                    sb.append("  - ").append(view).append(": ")
                      .append(String.join(", ", cols)).append("\n")
                );
            }
            if (metadata.getAnswer() != null && !metadata.getAnswer().isBlank()) {
                sb.append("Descripción: ").append(metadata.getAnswer()).append("\n");
            }
            sb.append("\n");
        }

        // Sección de datos reales
        if (data != null && data.isSuccess() && !data.getRows().isEmpty()) {
            sb.append("## DATOS RECUPERADOS\n");
            sb.append("Total de filas: ").append(data.getTotalRows()).append("\n");

            List<Map<String, Object>> sample = data.getRows().stream()
                    .limit(MAX_ROWS_IN_CONTEXT)
                    .collect(Collectors.toList());

            if (!data.getColumnNames().isEmpty()) {
                sb.append("Columnas: ")
                  .append(String.join(" | ", data.getColumnNames()))
                  .append("\n");
            }

            sb.append("Datos (primeras ").append(sample.size()).append(" filas):\n");
            sample.forEach(row -> {
                String line = row.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", "));
                sb.append("  { ").append(line).append(" }\n");
            });
        } else {
            sb.append("## DATOS\nNo se encontraron datos relevantes.\n");
        }

        return sb.toString();
    }

    /**
     * Construye contexto a partir de un Dataset local (subido por el usuario).
     */
    public String buildContextFromDataset(Dataset dataset, String question) {
        StringBuilder sb = new StringBuilder();

        sb.append("## PREGUNTA DEL USUARIO\n").append(question).append("\n\n");
        sb.append("## DATASET: ").append(dataset.getName()).append("\n");

        if (dataset.getDescription() != null) {
            sb.append("Descripción: ").append(dataset.getDescription()).append("\n");
        }

        if (dataset.getColumns() != null) {
            sb.append("Columnas: ").append(
                dataset.getColumns().stream()
                    .map(c -> c.getName() + "(" + c.getType() + ")")
                    .collect(Collectors.joining(", "))
            ).append("\n");
        }

        if (dataset.getStats() != null) {
            sb.append("Estadísticas:\n");
            sb.append("  Filas: ").append(dataset.getStats().getRowCount()).append("\n");
            if (dataset.getStats().getMeans() != null) {
                dataset.getStats().getMeans().forEach((col, mean) ->
                    sb.append("  Media ").append(col).append(": ").append(mean).append("\n")
                );
            }
        }

        if (dataset.getRows() != null && !dataset.getRows().isEmpty()) {
            List<Map<String, Object>> sample = dataset.getRows().stream()
                    .limit(MAX_ROWS_IN_CONTEXT)
                    .collect(Collectors.toList());
            sb.append("Datos (").append(sample.size()).append(" filas):\n");
            sample.forEach(row -> {
                String line = row.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", "));
                sb.append("  { ").append(line).append(" }\n");
            });
        }

        return sb.toString();
    }
}
