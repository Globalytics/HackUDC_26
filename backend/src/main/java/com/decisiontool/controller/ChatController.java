package com.decisiontool.controller;

import com.decisiontool.dto.ChatRequest;
import com.decisiontool.dto.ChatResponse;
import com.decisiontool.service.DenodoChatService;
import jakarta.validation.Valid;
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

        if (response.isError()) {
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Devuelve el historial de una sesión concreta.
     * GET /api/chat/{sessionId}/history
     */
    @GetMapping("/{sessionId}/history")
    public ResponseEntity<?> history(@PathVariable String sessionId) {
        return chatService.getSession(sessionId)
                .<ResponseEntity<?>>map(session -> ResponseEntity.ok(session.getMessages()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
