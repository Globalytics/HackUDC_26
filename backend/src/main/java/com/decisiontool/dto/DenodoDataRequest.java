package com.decisiontool.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class DenodoDataRequest {

    private String question;
    private String context;
    private String viewName;

    @JsonAlias({"sql_query", "sqlQuery"})
    private String sqlQuery;

    public DenodoDataRequest() {}

    public DenodoDataRequest(String question, String context, String viewName, String sqlQuery) {
        this.question = question;
        this.context = context;
        this.viewName = viewName;
        this.sqlQuery = sqlQuery;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String question, context, viewName, sqlQuery;

        public Builder question(String v) { this.question = v; return this; }
        public Builder context(String v)  { this.context = v; return this; }
        public Builder viewName(String v) { this.viewName = v; return this; }
        public Builder sqlQuery(String v) { this.sqlQuery = v; return this; }

        public DenodoDataRequest build() {
            return new DenodoDataRequest(question, context, viewName, sqlQuery);
        }
    }

    public String getQuestion() { return question; }
    public String getContext()  { return context; }
    public String getViewName() { return viewName; }
    public String getSqlQuery() { return sqlQuery; }

    public void setQuestion(String v) { this.question = v; }
    public void setContext(String v)  { this.context = v; }
    public void setViewName(String v) { this.viewName = v; }
    public void setSqlQuery(String v) { this.sqlQuery = v; }
}