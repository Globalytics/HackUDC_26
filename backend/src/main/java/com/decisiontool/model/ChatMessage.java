package com.decisiontool.model;

import java.time.LocalDateTime;

public class ChatMessage {

    public enum Role { USER, ASSISTANT }

    private String id;
    private Role role;
    private String content;
    private LocalDateTime timestamp;

    public ChatMessage() {}

    public ChatMessage(String id, Role role, String content, LocalDateTime timestamp) {
        this.id = id; this.role = role; this.content = content; this.timestamp = timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, content;
        private Role role;
        private LocalDateTime timestamp;
        public Builder id(String v)            { this.id = v; return this; }
        public Builder role(Role v)            { this.role = v; return this; }
        public Builder content(String v)       { this.content = v; return this; }
        public Builder timestamp(LocalDateTime v) { this.timestamp = v; return this; }
        public ChatMessage build() { return new ChatMessage(id, role, content, timestamp); }
    }

    public String getId()            { return id; }
    public Role getRole()            { return role; }
    public String getContent()       { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setId(String v)      { this.id = v; }
    public void setRole(Role v)      { this.role = v; }
    public void setContent(String v) { this.content = v; }
    public void setTimestamp(LocalDateTime v) { this.timestamp = v; }
}
