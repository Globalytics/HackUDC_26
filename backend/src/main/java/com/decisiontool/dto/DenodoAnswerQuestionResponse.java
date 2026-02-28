package com.decisiontool.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DenodoAnswerQuestionResponse {

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("sql_query")
    private String sqlQuery;

    @JsonProperty("query_explanation")
    private String queryExplanation;

    @JsonProperty("tokens")
    private Map<String, Object> tokens;

    @JsonProperty("related_questions")
    private List<String> relatedQuestions;

    @JsonProperty("execution_result")
    private Map<String, Object> executionResult;

    @JsonProperty("tables_used")
    private List<String> tablesUsed;

    @JsonProperty("sql_execution_time")
    private Double sqlExecutionTime;

    @JsonProperty("vector_store_search_time")
    private Double vectorStoreSearchTime;

    @JsonProperty("llm_time")
    private Double llmTime;

    @JsonProperty("total_execution_time")
    private Double totalExecutionTime;

    @JsonProperty("llm_provider")
    private String llmProvider;

    @JsonProperty("llm_model")
    private String llmModel;

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getSqlQuery() { return sqlQuery; }
    public void setSqlQuery(String sqlQuery) { this.sqlQuery = sqlQuery; }

    public String getQueryExplanation() { return queryExplanation; }
    public void setQueryExplanation(String queryExplanation) { this.queryExplanation = queryExplanation; }

    public Map<String, Object> getTokens() { return tokens; }
    public void setTokens(Map<String, Object> tokens) { this.tokens = tokens; }

    public List<String> getRelatedQuestions() { return relatedQuestions; }
    public void setRelatedQuestions(List<String> relatedQuestions) { this.relatedQuestions = relatedQuestions; }

    public Map<String, Object> getExecutionResult() { return executionResult; }
    public void setExecutionResult(Map<String, Object> executionResult) { this.executionResult = executionResult; }

    public List<String> getTablesUsed() { return tablesUsed; }
    public void setTablesUsed(List<String> tablesUsed) { this.tablesUsed = tablesUsed; }

    public Double getSqlExecutionTime() { return sqlExecutionTime; }
    public void setSqlExecutionTime(Double sqlExecutionTime) { this.sqlExecutionTime = sqlExecutionTime; }

    public Double getVectorStoreSearchTime() { return vectorStoreSearchTime; }
    public void setVectorStoreSearchTime(Double vectorStoreSearchTime) { this.vectorStoreSearchTime = vectorStoreSearchTime; }

    public Double getLlmTime() { return llmTime; }
    public void setLlmTime(Double llmTime) { this.llmTime = llmTime; }

    public Double getTotalExecutionTime() { return totalExecutionTime; }
    public void setTotalExecutionTime(Double totalExecutionTime) { this.totalExecutionTime = totalExecutionTime; }

    public String getLlmProvider() { return llmProvider; }
    public void setLlmProvider(String llmProvider) { this.llmProvider = llmProvider; }

    public String getLlmModel() { return llmModel; }
    public void setLlmModel(String llmModel) { this.llmModel = llmModel; }
}