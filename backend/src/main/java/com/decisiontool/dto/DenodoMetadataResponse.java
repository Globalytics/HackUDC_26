package com.decisiontool.dto;

import java.util.List;
import java.util.Map;

public class DenodoMetadataResponse {

    private String answer;
    private List<String> relevantViews;
    private Map<String, List<String>> columns;
    private boolean success;
    private String errorMessage;

    public DenodoMetadataResponse() {}

    public DenodoMetadataResponse(String answer, List<String> relevantViews,
                                  Map<String, List<String>> columns,
                                  boolean success, String errorMessage) {
        this.answer = answer; this.relevantViews = relevantViews; this.columns = columns;
        this.success = success; this.errorMessage = errorMessage;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String answer, errorMessage;
        private List<String> relevantViews;
        private Map<String, List<String>> columns;
        private boolean success;

        public Builder answer(String v)                        { this.answer = v; return this; }
        public Builder relevantViews(List<String> v)           { this.relevantViews = v; return this; }
        public Builder columns(Map<String, List<String>> v)    { this.columns = v; return this; }
        public Builder success(boolean v)                      { this.success = v; return this; }
        public Builder errorMessage(String v)                  { this.errorMessage = v; return this; }
        public DenodoMetadataResponse build() {
            return new DenodoMetadataResponse(answer, relevantViews, columns, success, errorMessage);
        }
    }

    public String getAnswer()                        { return answer; }
    public List<String> getRelevantViews()           { return relevantViews; }
    public Map<String, List<String>> getColumns()    { return columns; }
    public boolean isSuccess()                       { return success; }
    public String getErrorMessage()                  { return errorMessage; }
    public void setAnswer(String v)                  { this.answer = v; }
    public void setRelevantViews(List<String> v)     { this.relevantViews = v; }
    public void setColumns(Map<String, List<String>> v) { this.columns = v; }
    public void setSuccess(boolean v)                { this.success = v; }
    public void setErrorMessage(String v)            { this.errorMessage = v; }
}
