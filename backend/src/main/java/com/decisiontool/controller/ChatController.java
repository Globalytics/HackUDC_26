package com.decisiontool.controller;

import com.decisiontool.dto.ChatRequest;
import com.decisiontool.dto.ChatResponse;
import com.decisiontool.model.ChatMessage;
import com.decisiontool.model.ChatSession;
import com.decisiontool.service.DenodoChatService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final DenodoChatService chatService;

    public ChatController(DenodoChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Recibe una pregunta del usuario y devuelve la respuesta del motor RAG.
     * POST /api/chat
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("POST /api/chat - pregunta: '{}'", request.getQuestion());
        ChatResponse response = chatService.process(request);

        // Always return 200 so the frontend can inspect the error flag and message.
        // This prevents generic "Chat failed (HTTP 500)" messages and avoids
        // client-side "signal is aborted" errors when our backend already
        // produced a meaningful error payload.
        return ResponseEntity.ok(response);
    }

    /**
     * Devuelve el historial de una sesión concreta.
     * GET /api/chat/{sessionId}/history
     */
    @GetMapping("/{sessionId}/history")
    public ResponseEntity<List<ChatMessage>> history(@PathVariable String sessionId) {
    Optional<ChatSession> sessionOpt = chatService.getSession(sessionId);
    if (sessionOpt.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(sessionOpt.get().getMessages());
}
}
