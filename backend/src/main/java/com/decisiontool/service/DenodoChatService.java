package com.decisiontool.service;

import com.decisiontool.dto.ChatRequest;
import com.decisiontool.dto.ChatResponse;
import com.decisiontool.model.ChatMessage;
import com.decisiontool.model.ChatSession;
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

    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    public DenodoChatService(DenodoApiClient denodoApiClient,
                             DatasetContextBuilder contextBuilder) {
        this.denodoApiClient = denodoApiClient;
        this.contextBuilder  = contextBuilder;
    }

    public ChatResponse process(ChatRequest request) {
        log.info("Procesando pregunta: '{}'", request.getQuestion());

        ChatSession session = resolveSession(request.getSessionId());

        // ✅ VALIDACIÓN PREVENTIVA (evita 422 en Denodo por question null/blank)
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

            String answer = denodoApiClient.answerQuestion(body).block();
            if (answer == null) answer = "No se obtuvo respuesta de Denodo.";

            addToHistory(session, ChatMessage.Role.USER, question);
            addToHistory(session, ChatMessage.Role.ASSISTANT, answer);

            return ChatResponse.builder()
                    .sessionId(session.getId())
                    .answer(answer)
                    .error(false)
                    .build();

        } catch (Exception ex) {
            log.error("Error al procesar la pregunta", ex);

            // ✅ Mensaje no-nulo aunque ex.getMessage() sea null
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