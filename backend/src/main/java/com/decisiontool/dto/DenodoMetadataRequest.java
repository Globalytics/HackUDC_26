package com.decisiontool.dto;

public class DenodoMetadataRequest {

    private String question;
    private String context;

    public DenodoMetadataRequest() {}

    public DenodoMetadataRequest(String question, String context) {
        this.question = question; this.context = context;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String question, context;
        public Builder question(String v) { this.question = v; return this; }
        public Builder context(String v)  { this.context = v; return this; }
        public DenodoMetadataRequest build() { return new DenodoMetadataRequest(question, context); }
    }

    public String getQuestion() { return question; }
    public String getContext()  { return context; }
    public void setQuestion(String v) { this.question = v; }
    public void setContext(String v)  { this.context = v; }
}
