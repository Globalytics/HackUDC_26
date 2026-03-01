package com.decisiontool.service;

import com.decisiontool.dto.*;
import com.decisiontool.model.ChatMessage;
import com.decisiontool.model.ChatSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

        String vdpDatabaseName = request.getDatasetId();
        if (vdpDatabaseName == null || vdpDatabaseName.isBlank()) {
            return ChatResponse.builder()
                    .sessionId(session.getId())
                    .error(true)
                    .errorMessage("El campo 'datasetId' es obligatorio (vdp_database_names).")
                    .build();
        }

        try {
            // 1) METADATA primero
            DenodoMetadataRequest metadataReq = DenodoMetadataRequest.builder()
                    .question(question)
                    .context("") // mejor string vacío que null
                    .build();

            DenodoAnswerResponse metadata = denodoApiClient
                    .answerMetadataQuestion(vdpDatabaseName, metadataReq)
                    .block();

            if (metadata == null) {
                return ChatResponse.builder()
                        .sessionId(session.getId())
                        .error(true)
                        .errorMessage("Denodo /answerMetadataQuestion devolvió null.")
                        .build();
            }

            // Campos “oficiales” según schema
            String metadataAnswer = metadata.getAnswer();
            String sqlQuery = metadata.getSqlQuery(); // puede venir vacío en metadata
            String queryExplanation = metadata.getQueryExplanation();
            List<String> tablesUsed = metadata.getTablesUsed();
            String rawGraph = metadata.getRawGraph();

            // 2) Contexto base (si tu builder mete algo útil)
            String baseContext = contextBuilder.buildContext(metadata, null, question);
            if (baseContext == null) baseContext = "";

            // 3) Construimos CONTEXTO para /answerDataQuestion usando tables_used + raw_graph
            StringBuilder ctx = new StringBuilder();

            ctx.append("INSTRUCCIONES (para responder con DATOS):\n")
                    .append("- Genera UNA única VQL ejecutable y ejecútala.\n")
                    .append("- Devuelve como máximo 10 filas si aplica.\n")
                    .append("- Si el usuario pide 'más recientes' y no existe columna fecha/timestamp,\n")
                    .append("  usa como proxy un ID numérico (p.ej. *id, *_id, raceId) y ORDER BY DESC.\n")
                    .append("- Prioriza las tablas/vistas sugeridas en 'tables_used'.\n\n");

            if (tablesUsed != null && !tablesUsed.isEmpty()) {
                ctx.append("TABLAS/VISTAS SUGERIDAS (tables_used):\n");
                for (String t : tablesUsed) {
                    ctx.append("- ").append(t).append("\n");
                }
                ctx.append("\n");
            }

            if (rawGraph != null && !rawGraph.isBlank()) {
                ctx.append("GRAFO/INFO CRUDA (raw_graph):\n")
                        .append(rawGraph.trim())
                        .append("\n\n");
            }

            if (!baseContext.isBlank()) {
                ctx.append("CONTEXTO ADICIONAL (builder):\n")
                        .append(baseContext.trim())
                        .append("\n\n");
            }

            if (metadataAnswer != null && !metadataAnswer.isBlank()) {
                ctx.append("RESUMEN METADATA (answer de metadata):\n")
                        .append(metadataAnswer.trim())
                        .append("\n\n");
            }

            // Si metadata trae SQL (a veces), lo incluimos como pista, pero NO dependemos de ello
            if (sqlQuery != null && !sqlQuery.isBlank()) {
                ctx.append("SQL/VQL sugerida por metadata (si es útil):\n")
                        .append(sqlQuery.trim())
                        .append("\n\n");
            }

            if (queryExplanation != null && !queryExplanation.isBlank()) {
                ctx.append("Explicación de la SQL/VQL:\n")
                        .append(queryExplanation.trim())
                        .append("\n\n");
            }

            String context = ctx.toString().trim();

            // 4) DATA después (la API de data ejecuta VQL y trae resultados)
            DenodoDataRequest dataReq = DenodoDataRequest.builder()
                    .question(question)
                    .context(context)
                    .viewName(null)  // no dependemos de esto
                    .sqlQuery(null)  // no dependemos de esto
                    .build();

            DenodoAnswerResponse data = denodoApiClient
                    .answerDataQuestion(vdpDatabaseName, dataReq)
                    .block();

            // Respuesta final (prioriza data.answer)
            String finalAnswer;
            if (data != null && data.getAnswer() != null && !data.getAnswer().isBlank()) {
                finalAnswer = data.getAnswer();
            } else if (metadataAnswer != null && !metadataAnswer.isBlank()) {
                finalAnswer = metadataAnswer;
            } else {
                finalAnswer = "No se obtuvo respuesta de Denodo.";
            }

            // ✅ tableData: extraer filas desde execution_result (sin tocar ChatResponse)
            List<Map<String, Object>> tableData = null;

            if (data != null && data.getExecutionResult() != null) {
                Object rowsObj = data.getExecutionResult().get("rows");
                if (rowsObj == null) rowsObj = data.getExecutionResult().get("data");
                if (rowsObj == null) rowsObj = data.getExecutionResult().get("result");

                if (rowsObj instanceof List<?>) {
                    List<?> list = (List<?>) rowsObj;

                    List<Map<String, Object>> parsed = new ArrayList<>();
                    for (Object item : list) {
                        if (item instanceof Map<?, ?>) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> row = (Map<String, Object>) item;
                            parsed.add(row);
                        }
                    }

                    if (!parsed.isEmpty()) {
                        tableData = parsed;
                    }
                }
            }

            // metrics: para depurar el flujo y justificar eficiencia
            Map<String, Object> metrics = new LinkedHashMap<>();
            metrics.put("tables_used", tablesUsed);
            metrics.put("raw_graph_present", rawGraph != null && !rawGraph.isBlank());
            metrics.put("metadata_sql_query_present", sqlQuery != null && !sqlQuery.isBlank());
            metrics.put("metadata_query_explanation_present", queryExplanation != null && !queryExplanation.isBlank());
            metrics.put("context_length", context.length());

            if (data != null && data.getExecutionResult() != null) {
                metrics.put("execution_result_keys", data.getExecutionResult().keySet());
            }

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
        String id = (sessionId != null) ? sessionId : UUID.randomUUID().toString();
        ChatSession session = ChatSession.builder()
                .id(id)
                .createdAt(LocalDateTime.now())
                .lastActivity(LocalDateTime.now())
                .build();
        sessions.put(id, session);
        return session;
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
}