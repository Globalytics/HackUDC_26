package com.decisiontool.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DenodoAnswerResponse {

    private String answer;

    @JsonAlias({"sql_query", "sqlQuery"})
    private String sqlQuery;

    @JsonAlias({"query_explanation", "queryExplanation"})
    private String queryExplanation;

    private Map<String, Object> tokens;

    @JsonAlias({"execution_result", "executionResult"})
    private Map<String, Object> executionResult;

    @JsonAlias({"related_questions", "relatedQuestions"})
    private List<String> relatedQuestions;

    @JsonAlias({"tables_used", "tablesUsed"})
    private List<String> tablesUsed;

    @JsonAlias({"raw_graph", "rawGraph"})
    private String rawGraph;

    @JsonAlias({"sql_execution_time", "sqlExecutionTime"})
    private Double sqlExecutionTime;

    @JsonAlias({"vector_store_search_time", "vectorStoreSearchTime"})
    private Double vectorStoreSearchTime;

    @JsonAlias({"llm_time", "llmTime"})
    private Double llmTime;

    @JsonAlias({"total_execution_time", "totalExecutionTime"})
    private Double totalExecutionTime;

    @JsonAlias({"llm_provider", "llmProvider"})
    private String llmProvider;

    @JsonAlias({"llm_model", "llmModel"})
    private String llmModel;

    // Getters/Setters
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getSqlQuery() { return sqlQuery; }
    public void setSqlQuery(String sqlQuery) { this.sqlQuery = sqlQuery; }

    public String getQueryExplanation() { return queryExplanation; }
    public void setQueryExplanation(String queryExplanation) { this.queryExplanation = queryExplanation; }

    public Map<String, Object> getTokens() { return tokens; }
    public void setTokens(Map<String, Object> tokens) { this.tokens = tokens; }

    public Map<String, Object> getExecutionResult() { return executionResult; }
    public void setExecutionResult(Map<String, Object> executionResult) { this.executionResult = executionResult; }

    public List<String> getRelatedQuestions() { return relatedQuestions; }
    public void setRelatedQuestions(List<String> relatedQuestions) { this.relatedQuestions = relatedQuestions; }

    public List<String> getTablesUsed() { return tablesUsed; }
    public void setTablesUsed(List<String> tablesUsed) { this.tablesUsed = tablesUsed; }

    public String getRawGraph() { return rawGraph; }
    public void setRawGraph(String rawGraph) { this.rawGraph = rawGraph; }

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