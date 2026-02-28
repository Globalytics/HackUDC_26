package com.decisiontool.dto;

import jakarta.validation.constraints.NotBlank;

public class DecisionRequest {

    @NotBlank(message = "El problema no puede estar vacío")
    private String problem;
    private String datasetId;
    private String criteria;

    public DecisionRequest() {}

    public DecisionRequest(String problem, String datasetId, String criteria) {
        this.problem = problem; this.datasetId = datasetId; this.criteria = criteria;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String problem, datasetId, criteria;
        public Builder problem(String v)   { this.problem = v; return this; }
        public Builder datasetId(String v) { this.datasetId = v; return this; }
        public Builder criteria(String v)  { this.criteria = v; return this; }
        public DecisionRequest build() { return new DecisionRequest(problem, datasetId, criteria); }
    }

    public String getProblem()   { return problem; }
    public String getDatasetId() { return datasetId; }
    public String getCriteria()  { return criteria; }
    public void setProblem(String v)   { this.problem = v; }
    public void setDatasetId(String v) { this.datasetId = v; }
    public void setCriteria(String v)  { this.criteria = v; }
}
