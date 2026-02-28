package com.decisiontool.service;

import com.decisiontool.dto.DenodoAnswerQuestionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DenodoAccessService {

    private static final String QUESTION_LIST_DATABASES =
            "Dime las bases de datos a las que tengo acceso. Devuelve SOLO los nombres de bases de datos.";

    // Regex estricta: exige al menos un carácter alfanumérico a ambos lados del _
    private static final Pattern DB_NAME_PATTERN = Pattern.compile("\\b([A-Za-z0-9]+_[A-Za-z0-9_]+)\\b");

    private final DenodoApiClient denodoApiClient;
    private final ObjectMapper objectMapper;

    public DenodoAccessService(DenodoApiClient denodoApiClient, ObjectMapper objectMapper) {
        this.denodoApiClient = denodoApiClient;
        this.objectMapper = objectMapper;
    }

    public List<String> listAccessibleDatabases() {
        Map<String, Object> body = Map.of("question", QUESTION_LIST_DATABASES);

        String rawJson = denodoApiClient.answerQuestion(body).block();
        if (rawJson == null || rawJson.isBlank()) return List.of();

        DenodoAnswerQuestionResponse resp;
        try {
            resp = objectMapper.readValue(rawJson, DenodoAnswerQuestionResponse.class);
        } catch (JsonProcessingException e) {
            return List.of();
        }

        Set<String> dbs = new LinkedHashSet<>();

        // 1) Fuente buena: execution_result (estructurado)
        dbs.addAll(extractDbNamesFromExecutionResult(resp.getExecutionResult()));

        // 2) Fuente buena: tables_used (tipo "f1_races.races")
        if (dbs.isEmpty()) {
            dbs.addAll(extractDbNamesFromTablesUsed(resp.getTablesUsed()));
        }

        // 3) Fallback controlado: regex estricta sobre "answer"
        if (dbs.isEmpty()) {
            dbs.addAll(extractDbNamesFromAnswerStrict(resp.getAnswer()));
        }

        return dbs.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractDbNamesFromExecutionResult(Map<String, Object> executionResult) {
        Set<String> out = new LinkedHashSet<>();
        if (executionResult == null || executionResult.isEmpty()) return out;

        // Recorre profundo buscando maps con clave "value" (String)
        collectValueStringsDeep(executionResult, out);

        // Filtra a nombres con underscore (tipo f1_races)
        out.removeIf(s -> !DB_NAME_PATTERN.matcher(s).matches());

        return out;
    }

    @SuppressWarnings("unchecked")
    private void collectValueStringsDeep(Object node, Set<String> out) {
        if (node == null) return;

        if (node instanceof Map<?, ?> map) {
            Object value = map.get("value");
            if (value instanceof String s) out.add(s);

            for (Object v : map.values()) collectValueStringsDeep(v, out);
            return;
        }

        if (node instanceof List<?> list) {
            for (Object item : list) collectValueStringsDeep(item, out);
        }
    }

    private Set<String> extractDbNamesFromTablesUsed(List<String> tablesUsed) {
        Set<String> out = new LinkedHashSet<>();
        if (tablesUsed == null) return out;

        for (String t : tablesUsed) {
            if (t == null) continue;
            // Ej: "f1_races.races" => "f1_races"
            int dot = t.indexOf('.');
            if (dot > 0) {
                String db = t.substring(0, dot);
                if (DB_NAME_PATTERN.matcher(db).matches()) out.add(db);
            }
        }
        return out;
    }

    private Set<String> extractDbNamesFromAnswerStrict(String answer) {
        Set<String> out = new LinkedHashSet<>();
        if (answer == null || answer.isBlank()) return out;

        Matcher m = DB_NAME_PATTERN.matcher(answer);
        while (m.find()) {
            out.add(m.group(1));
        }
        return out;
    }
}