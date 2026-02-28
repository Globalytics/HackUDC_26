package com.decisiontool.dto;

public class DenodoDataRequest {

    private String question;
    private String context;
    private String viewName;

    public DenodoDataRequest() {}

    public DenodoDataRequest(String question, String context, String viewName) {
        this.question = question; this.context = context; this.viewName = viewName;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String question, context, viewName;
        public Builder question(String v) { this.question = v; return this; }
        public Builder context(String v)  { this.context = v; return this; }
        public Builder viewName(String v) { this.viewName = v; return this; }
        public DenodoDataRequest build() { return new DenodoDataRequest(question, context, viewName); }
    }

    public String getQuestion() { return question; }
    public String getContext()  { return context; }
    public String getViewName() { return viewName; }
    public void setQuestion(String v) { this.question = v; }
    public void setContext(String v)  { this.context = v; }
    public void setViewName(String v) { this.viewName = v; }
}
