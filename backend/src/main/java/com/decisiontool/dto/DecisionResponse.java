package com.decisiontool.dto;

import java.util.List;
import java.util.Map;

public class DecisionResponse {

    private String recommendation;
    private String justification;
    private Map<String, Object> metrics;
    private List<Map<String, Object>> alternatives;
    private boolean error;
    private String errorMessage;

    public DecisionResponse() {}

    public DecisionResponse(String recommendation, String justification,
                            Map<String, Object> metrics, List<Map<String, Object>> alternatives,
                            boolean error, String errorMessage) {
        this.recommendation = recommendation; this.justification = justification;
        this.metrics = metrics; this.alternatives = alternatives;
        this.error = error; this.errorMessage = errorMessage;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String recommendation, justification, errorMessage;
        private Map<String, Object> metrics;
        private List<Map<String, Object>> alternatives;
        private boolean error;

        public Builder recommendation(String v)                  { this.recommendation = v; return this; }
        public Builder justification(String v)                   { this.justification = v; return this; }
        public Builder metrics(Map<String, Object> v)            { this.metrics = v; return this; }
        public Builder alternatives(List<Map<String, Object>> v) { this.alternatives = v; return this; }
        public Builder error(boolean v)                          { this.error = v; return this; }
        public Builder errorMessage(String v)                    { this.errorMessage = v; return this; }
        public DecisionResponse build() {
            return new DecisionResponse(recommendation, justification, metrics, alternatives, error, errorMessage);
        }
    }

    public String getRecommendation()               { return recommendation; }
    public String getJustification()                { return justification; }
    public Map<String, Object> getMetrics()         { return metrics; }
    public List<Map<String, Object>> getAlternatives() { return alternatives; }
    public boolean isError()                        { return error; }
    public String getErrorMessage()                 { return errorMessage; }
    public void setRecommendation(String v)         { this.recommendation = v; }
    public void setJustification(String v)          { this.justification = v; }
    public void setMetrics(Map<String, Object> v)   { this.metrics = v; }
    public void setAlternatives(List<Map<String, Object>> v) { this.alternatives = v; }
    public void setError(boolean v)                 { this.error = v; }
    public void setErrorMessage(String v)           { this.errorMessage = v; }
}
