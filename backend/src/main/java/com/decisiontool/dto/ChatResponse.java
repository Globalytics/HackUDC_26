package com.decisiontool.dto;

import java.util.List;
import java.util.Map;

public class ChatResponse {

    private String sessionId;
    private String answer;
    private String datasetUsed;
    private List<Map<String, Object>> tableData;
    private Map<String, Object> metrics;
    private boolean error;
    private String errorMessage;

    public ChatResponse() {}
    public ChatResponse(String sessionId, String answer, String datasetUsed,
                        List<Map<String, Object>> tableData, Map<String, Object> metrics,
                        boolean error, String errorMessage) {
        this.sessionId = sessionId; this.answer = answer; this.datasetUsed = datasetUsed;
        this.tableData = tableData; this.metrics = metrics;
        this.error = error; this.errorMessage = errorMessage;
    }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String sessionId, answer, datasetUsed, errorMessage;
        private List<Map<String, Object>> tableData;
        private Map<String, Object> metrics;
        private boolean error;
        public Builder sessionId(String v)                      { this.sessionId = v; return this; }
        public Builder answer(String v)                         { this.answer = v; return this; }
        public Builder datasetUsed(String v)                    { this.datasetUsed = v; return this; }
        public Builder tableData(List<Map<String, Object>> v)   { this.tableData = v; return this; }
        public Builder metrics(Map<String, Object> v)           { this.metrics = v; return this; }
        public Builder error(boolean v)                         { this.error = v; return this; }
        public Builder errorMessage(String v)                   { this.errorMessage = v; return this; }
        public ChatResponse build() {
            return new ChatResponse(sessionId, answer, datasetUsed, tableData, metrics, error, errorMessage);
        }
    }

    public String getSessionId()                        { return sessionId; }
    public String getAnswer()                           { return answer; }
    public String getDatasetUsed()                      { return datasetUsed; }
    public List<Map<String, Object>> getTableData()     { return tableData; }
    public Map<String, Object> getMetrics()             { return metrics; }
    public boolean isError()                            { return error; }
    public String getErrorMessage()                     { return errorMessage; }
    public void setSessionId(String v)                  { this.sessionId = v; }
    public void setAnswer(String v)                     { this.answer = v; }
    public void setDatasetUsed(String v)                { this.datasetUsed = v; }
    public void setTableData(List<Map<String, Object>> v){ this.tableData = v; }
    public void setMetrics(Map<String, Object> v)       { this.metrics = v; }
    public void setError(boolean v)                     { this.error = v; }
    public void setErrorMessage(String v)               { this.errorMessage = v; }
}
