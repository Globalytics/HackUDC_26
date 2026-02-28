package com.decisiontool.dto;

import java.util.List;
import java.util.Map;

public class DenodoDataResponse {

    private String answer;
    private List<Map<String, Object>> rows;
    private List<String> columnNames;
    private int totalRows;
    private boolean success;
    private String errorMessage;

    public DenodoDataResponse() {}

    public DenodoDataResponse(String answer, List<Map<String, Object>> rows, List<String> columnNames,
                              int totalRows, boolean success, String errorMessage) {
        this.answer = answer; this.rows = rows; this.columnNames = columnNames;
        this.totalRows = totalRows; this.success = success; this.errorMessage = errorMessage;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String answer, errorMessage;
        private List<Map<String, Object>> rows;
        private List<String> columnNames;
        private int totalRows;
        private boolean success;

        public Builder answer(String v)                        { this.answer = v; return this; }
        public Builder rows(List<Map<String, Object>> v)       { this.rows = v; return this; }
        public Builder columnNames(List<String> v)             { this.columnNames = v; return this; }
        public Builder totalRows(int v)                        { this.totalRows = v; return this; }
        public Builder success(boolean v)                      { this.success = v; return this; }
        public Builder errorMessage(String v)                  { this.errorMessage = v; return this; }
        public DenodoDataResponse build() {
            return new DenodoDataResponse(answer, rows, columnNames, totalRows, success, errorMessage);
        }
    }

    public String getAnswer()                        { return answer; }
    public List<Map<String, Object>> getRows()       { return rows; }
    public List<String> getColumnNames()             { return columnNames; }
    public int getTotalRows()                        { return totalRows; }
    public boolean isSuccess()                       { return success; }
    public String getErrorMessage()                  { return errorMessage; }
    public void setAnswer(String v)                  { this.answer = v; }
    public void setRows(List<Map<String, Object>> v) { this.rows = v; }
    public void setColumnNames(List<String> v)       { this.columnNames = v; }
    public void setTotalRows(int v)                  { this.totalRows = v; }
    public void setSuccess(boolean v)                { this.success = v; }
    public void setErrorMessage(String v)            { this.errorMessage = v; }
}
