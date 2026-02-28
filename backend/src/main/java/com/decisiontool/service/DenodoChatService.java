package com.decisiontool.service;

import com.decisiontool.dto.ChatRequest;
import com.decisiontool.dto.ChatResponse;
import com.decisiontool.dto.DenodoAnswerQuestionResponse;
import com.decisiontool.model.ChatMessage;
import com.decisiontool.model.ChatSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DenodoChatService {

    private static final Logger log = LoggerFactory.getLogger(DenodoChatService.class);

    private final DenodoApiClient denodoApiClient;
    private final DatasetContextBuilder contextBuilder;
    private final ObjectMapper objectMapper;

    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    public DenodoChatService(DenodoApiClient denodoApiClient,
                             DatasetContextBuilder contextBuilder,
                             ObjectMapper objectMapper) {
        this.denodoApiClient = denodoApiClient;
        this.contextBuilder = contextBuilder;
        this.objectMapper = objectMapper;
    }

    public ChatResponse process(ChatRequest request) {
        log.info("Procesando pregunta: '{}'", request.getQuestion());

        ChatSession session = resolveSession(request.getSessionId());

        // ✅ VALIDACIÓN PREVENTIVA
        String question = request.getQuestion();
        if (question == null || question.isBlank()) {
            return ChatResponse.builder()
                    .sessionId(session.getId())
                    .error(true)
                    .errorMessage("El campo 'question' (o 'message') es obligatorio.")
                    .build();
        }

        try {
            Map<String, Object> body = Map.of("question", question);

            // 🔹 Denodo te devuelve un String que en realidad es JSON (a veces escapado)
            String raw = denodoApiClient.answerQuestion(body).block();
            if (raw == null) raw = "";

            DenodoAnswerQuestionResponse denodo = tryParseDenodo(raw);

            // ✅ Answer limpio
            String finalAnswer = extractBestAnswer(raw, denodo);

            // ✅ tableData (si Denodo trae execution_result)
            List<Map<String, Object>> tableData = extractTableData(denodo);

            // ✅ metrics (tiempos, tokens, sql, etc.)
            Map<String, Object> metrics = buildMetrics(denodo);

            addToHistory(session, ChatMessage.Role.USER, question);
            addToHistory(session, ChatMessage.Role.ASSISTANT, finalAnswer);

            return ChatResponse.builder()
                    .sessionId(session.getId())
                    .answer(finalAnswer)
                    .tableData(tableData)
                    .metrics(metrics)
                    .error(false)
                    .build();

        } catch (Exception ex) {
            log.error("Error al procesar la pregunta", ex);
            String detail = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getSimpleName();

            return ChatResponse.builder()
                    .sessionId(session.getId())
                    .error(true)
                    .errorMessage("Error al conectar con Denodo: " + detail)
                    .build();
        }
    }

    public Optional<ChatSession> getSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    private ChatSession resolveSession(String sessionId) {
        if (sessionId != null && sessions.containsKey(sessionId)) {
            return sessions.get(sessionId);
        }
        String id = sessionId != null ? sessionId : UUID.randomUUID().toString();
        ChatSession session = ChatSession.builder()
                .id(id)
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();
        sessions.put(id, session);
        return session;
    }

    private List<Map<String, Object>> extractTableData(DenodoAnswerQuestionResponse denodo) {
        if (denodo == null || denodo.getExecutionResult() == null) return null;

        // execution_result suele ser {"Row 1":[{...}], "Row 2":[{...}]...}
        Object first = denodo.getExecutionResult().values().stream().findFirst().orElse(null);
        if (first == null) return null;

        try {
            return objectMapper.convertValue(first, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.debug("No se pudo convertir execution_result a tableData", e);
            return null;
        }
    }

    private void addToHistory(ChatSession session, ChatMessage.Role role, String content) {
        ChatMessage msg = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .role(role)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        session.addMessage(msg);
    }

    /**
     * Denodo a veces devuelve:
     *  1) JSON normal: {"answer":"...","sql_query":"..."}
     *  2) JSON escapado dentro de un string: "{\"answer\":\"...\"...}"
     *
     * Este método intenta parsear ambas variantes.
     */
    private DenodoAnswerQuestionResponse tryParseDenodo(String raw) {
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) return null;

        try {
            // Caso 1: ya es JSON objeto
            if (trimmed.startsWith("{")) {
                return objectMapper.readValue(trimmed, DenodoAnswerQuestionResponse.class);
            }

            // Caso 2: viene como string JSON escapado (p.ej. "\"{\\\"answer\\\":...}\"")
            // Intentamos des-serializar a String primero, y luego a objeto.
            if (trimmed.startsWith("\"")) {
                String unescapedJson = objectMapper.readValue(trimmed, String.class);
                if (unescapedJson != null && unescapedJson.trim().startsWith("{")) {
                    return objectMapper.readValue(unescapedJson, DenodoAnswerQuestionResponse.class);
                }
            }
        } catch (Exception e) {
            log.debug("No se pudo parsear respuesta Denodo como JSON. Se devolverá raw.", e);
        }
        return null;
    }

    private String extractBestAnswer(String raw, DenodoAnswerQuestionResponse denodo) {
        if (denodo != null && denodo.getAnswer() != null && !denodo.getAnswer().isBlank()) {
            return denodo.getAnswer();
        }

        // Si el raw era un JSON escapado dentro de comillas, intentamos extraer el "answer"
        // ya cubierto por tryParseDenodo; si falló, devolvemos raw sin romper.
        return raw.isBlank() ? "No se obtuvo respuesta de Denodo." : raw;
    }

    private Map<String, Object> buildMetrics(DenodoAnswerQuestionResponse denodo) {
        if (denodo == null) return null;

        Map<String, Object> metrics = new LinkedHashMap<>();
        if (denodo.getSqlExecutionTime() != null) metrics.put("sql_execution_time", denodo.getSqlExecutionTime());
        if (denodo.getVectorStoreSearchTime() != null) metrics.put("vector_store_search_time", denodo.getVectorStoreSearchTime());
        if (denodo.getLlmTime() != null) metrics.put("llm_time", denodo.getLlmTime());
        if (denodo.getTotalExecutionTime() != null) metrics.put("total_execution_time", denodo.getTotalExecutionTime());

        if (denodo.getLlmProvider() != null) metrics.put("llm_provider", denodo.getLlmProvider());
        if (denodo.getLlmModel() != null) metrics.put("llm_model", denodo.getLlmModel());

        if (denodo.getTokens() != null) metrics.put("tokens", denodo.getTokens());
        if (denodo.getTablesUsed() != null) metrics.put("tables_used", denodo.getTablesUsed());
        if (denodo.getSqlQuery() != null) metrics.put("sql_query", denodo.getSqlQuery());
        if (denodo.getQueryExplanation() != null) metrics.put("query_explanation", denodo.getQueryExplanation());
        if (denodo.getRelatedQuestions() != null) metrics.put("related_questions", denodo.getRelatedQuestions());

        return metrics.isEmpty() ? null : metrics;
    }
}