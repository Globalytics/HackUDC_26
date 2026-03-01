package com.decisiontool.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DenodoMetadataResponse {
    private String answer;

    @JsonAlias({"sql_query","sqlQuery"})
    private String sqlQuery;

    @JsonAlias({"query_explanation","queryExplanation"})
    private String queryExplanation;

    @JsonAlias({"tokens"})
    private Map<String, Object> tokens;

    @JsonAlias({"execution_result","executionResult"})
    private Map<String, Object> executionResult;

    @JsonAlias({"related_questions","relatedQuestions"})
    private List<String> relatedQuestions;

    @JsonAlias({"tables_used","tablesUsed"})
    private List<String> tablesUsed;

    @JsonAlias({"raw_graph","rawGraph"})
    private String rawGraph;

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    public String getSqlQuery() {
        return this.sqlQuery;
    }

    public void setSqlQuery(final String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getQueryExplanation() {
        return this.queryExplanation;
    }

    public void setQueryExplanation(final String queryExplanation) {
        this.queryExplanation = queryExplanation;
    }

    public Map<String, Object> getTokens() {
        return this.tokens;
    }

    public void setTokens(final Map<String, Object> tokens) {
        this.tokens = tokens;
    }

    public Map<String, Object> getExecutionResult() {
        return this.executionResult;
    }

    public void setExecutionResult(final Map<String, Object> executionResult) {
        this.executionResult = executionResult;
    }

    public List<String> getRelatedQuestions() {
        return this.relatedQuestions;
    }

    public void setRelatedQuestions(final List<String> relatedQuestions) {
        this.relatedQuestions = relatedQuestions;
    }

    public List<String> getTablesUsed() {
        return this.tablesUsed;
    }

    public void setTablesUsed(final List<String> tablesUsed) {
        this.tablesUsed = tablesUsed;
    }

    public String getRawGraph() {
        return this.rawGraph;
    }

    public void setRawGraph(final String rawGraph) {
        this.rawGraph = rawGraph;
    }
}