package com.decisiontool.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatSession {

    private String id;
    private String activeDatasetId;
    private List<ChatMessage> messages = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;

    public ChatSession() {}

    public ChatSession(String id, String activeDatasetId, List<ChatMessage> messages,
                       LocalDateTime createdAt, LocalDateTime lastActivity) {
        this.id = id; this.activeDatasetId = activeDatasetId;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.createdAt = createdAt; this.lastActivity = lastActivity;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, activeDatasetId;
        private List<ChatMessage> messages = new ArrayList<>();
        private LocalDateTime createdAt, lastActivity;
        public Builder id(String v)                    { this.id = v; return this; }
        public Builder activeDatasetId(String v)       { this.activeDatasetId = v; return this; }
        public Builder messages(List<ChatMessage> v)   { this.messages = v; return this; }
        public Builder createdAt(LocalDateTime v)      { this.createdAt = v; return this; }
        public Builder lastActivity(LocalDateTime v)   { this.lastActivity = v; return this; }
        public ChatSession build() {
            return new ChatSession(id, activeDatasetId, messages, createdAt, lastActivity);
        }
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        this.lastActivity = LocalDateTime.now();
    }

    public String getId()                       { return id; }
    public String getActiveDatasetId()          { return activeDatasetId; }
    public List<ChatMessage> getMessages()      { return messages; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public LocalDateTime getLastActivity()      { return lastActivity; }
    public void setId(String v)                 { this.id = v; }
    public void setActiveDatasetId(String v)    { this.activeDatasetId = v; }
    public void setMessages(List<ChatMessage> v){ this.messages = v; }
    public void setCreatedAt(LocalDateTime v)   { this.createdAt = v; }
    public void setLastActivity(LocalDateTime v){ this.lastActivity = v; }
}
