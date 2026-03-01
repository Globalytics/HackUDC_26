package com.decisiontool.service;

import com.decisiontool.config.DenodoConfig;
import com.decisiontool.dto.DenodoAnswerQuestionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class DenodoAccessService {

    private static final String QUESTION_LIST_DATABASES =
            "Dime las bases de datos a las que tengo acceso. Devuelve SOLO los nombres de bases de datos.";

    private static final Pattern DB_NAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_]{0,63}$");

    private static final Set<String> DENYLIST = Set.of("admin", "a");

    private final DenodoApiClient denodoApiClient;
    private final ObjectMapper objectMapper;
    private final DenodoConfig denodoConfig;

    public DenodoAccessService(DenodoApiClient denodoApiClient,
                               ObjectMapper objectMapper,
                               DenodoConfig denodoConfig) {
        this.denodoApiClient = denodoApiClient;
        this.objectMapper = objectMapper;
        this.denodoConfig = denodoConfig;
    }

    public List<String> listAccessibleDatabases() {
        // ✅ SIEMPRE usar vdpDatabaseName desde config
        String vdpDatabaseName = denodoConfig.getVdpDatabaseName();
        if (vdpDatabaseName == null || vdpDatabaseName.isBlank()) {
            // Mejor fallar controlado que reventar con IllegalArgumentException
            return List.of();
        }

        Map<String, Object> body = Map.of("question", QUESTION_LIST_DATABASES);

        String rawJson = denodoApiClient
                .answerQuestion(denodoConfig.getVdpDatabaseName(), body).block();

        if (rawJson == null || rawJson.isBlank()) return List.of();

        DenodoAnswerQuestionResponse resp;
        try {
            resp = objectMapper.readValue(rawJson, DenodoAnswerQuestionResponse.class);
        } catch (JsonProcessingException e) {
            return List.of();
        }

        Set<String> dbs = new LinkedHashSet<>();

        dbs.addAll(extractDbNamesFromExecutionResult(resp.getExecutionResult()));

        if (dbs.isEmpty()) {
            dbs.addAll(extractDbNamesFromTablesUsed(resp.getTablesUsed()));
        }

        if (dbs.isEmpty()) {
            dbs.addAll(extractDbNamesFromAnswerLoose(resp.getAnswer()));
        }

        return dbs.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(this::isValidDbName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Set<String> extractDbNamesFromExecutionResult(Map<String, Object> executionResult) {
        Set<String> out = new LinkedHashSet<>();
        if (executionResult == null || executionResult.isEmpty()) return out;

        collectValueStringsDeep(executionResult, out);

        out.removeIf(s -> !isValidDbName(s));
        return out;
    }

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

            int dot = t.indexOf('.');
            if (dot > 0) {
                String db = t.substring(0, dot).trim();
                if (isValidDbName(db)) out.add(db);
            }
        }
        return out;
    }

    private Set<String> extractDbNamesFromAnswerLoose(String answer) {
        Set<String> out = new LinkedHashSet<>();
        if (answer == null || answer.isBlank()) return out;

        String[] tokens = answer.split("[\\s,;\\n\\r\\t\\-•]+");
        for (String tok : tokens) {
            String s = tok.trim();
            if (isValidDbName(s)) out.add(s);
        }
        return out;
    }

    private boolean isValidDbName(String s) {
        if (s == null) return false;
        String v = s.trim();
        if (v.isBlank()) return false;
        if (!DB_NAME_PATTERN.matcher(v).matches()) return false;
        return !DENYLIST.contains(v.toLowerCase());
    }
}