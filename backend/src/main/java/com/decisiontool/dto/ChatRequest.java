package com.decisiontool.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ChatRequest {

    @JsonAlias("message")   // 👈 Permite que "message" se mapee a "question"
    private String question;

    private String sessionId;
    private String datasetId;

    public ChatRequest() {}

    public ChatRequest(String question, String sessionId, String datasetId) {
        this.question = question;
        this.sessionId = sessionId;
        this.datasetId = datasetId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String question, sessionId, datasetId;

        public Builder question(String v)  { this.question = v; return this; }
        public Builder sessionId(String v) { this.sessionId = v; return this; }
        public Builder datasetId(String v) { this.datasetId = v; return this; }

        public ChatRequest build() {
            return new ChatRequest(question, sessionId, datasetId);
        }
    }

    public String getQuestion()  { return question; }
    public String getSessionId() { return sessionId; }
    public String getDatasetId() { return datasetId; }

    public void setQuestion(String v)  { this.question = v; }
    public void setSessionId(String v) { this.sessionId = v; }
    public void setDatasetId(String v) { this.datasetId = v; }
}