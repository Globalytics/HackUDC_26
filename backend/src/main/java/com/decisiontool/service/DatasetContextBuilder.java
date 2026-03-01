package com.decisiontool.service;

import com.decisiontool.dto.DenodoAnswerResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatasetContextBuilder {

    /**
     * Construye un contexto textual a partir de la respuesta de /answerMetadataQuestion.
     * Este contexto se usa luego para guiar /answerDataQuestion (mejor precisión y menos coste).
     */
    public String buildContext(DenodoAnswerResponse metadata, Object ignoredData, String question) {
        if (metadata == null) return "";

        StringBuilder sb = new StringBuilder();

        // 1) Tablas/vistas sugeridas por el endpoint (si existen)
        List<String> tablesUsed = metadata.getTablesUsed();
        if (tablesUsed != null && !tablesUsed.isEmpty()) {
            sb.append("Tablas/Vistas relevantes:\n");
            for (String t : tablesUsed) {
                sb.append("- ").append(t).append("\n");
            }
            sb.append("\n");
        }

        // 2) Raw graph (si viene) — suele contener estructura útil
        String rawGraph = metadata.getRawGraph();
        if (rawGraph != null && !rawGraph.isBlank()) {
            sb.append("raw_graph:\n");
            sb.append(rawGraph.trim()).append("\n\n");
        }

        // 3) Resumen que devuelve metadata
        String answer = metadata.getAnswer();
        if (answer != null && !answer.isBlank()) {
            sb.append("Resumen de metadata:\n");
            sb.append(answer.trim()).append("\n\n");
        }

        // 4) Si metadata trae query_explanation, también ayuda
        String queryExplanation = metadata.getQueryExplanation();
        if (queryExplanation != null && !queryExplanation.isBlank()) {
            sb.append("query_explanation:\n");
            sb.append(queryExplanation.trim()).append("\n\n");
        }

        // 5) Opcional: si trae sql_query (a veces vacío en metadata)
        String sqlQuery = metadata.getSqlQuery();
        if (sqlQuery != null && !sqlQuery.isBlank()) {
            sb.append("sql_query (si aplica):\n");
            sb.append(sqlQuery.trim()).append("\n\n");
        }

        return sb.toString().trim();
    }
}