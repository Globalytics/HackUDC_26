package com.decisiontool.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DenodoGetMetadataResponse {

    @JsonProperty("db_schema_json")
    private Map<String, Object> dbSchemaJson;

    @JsonProperty("db_schema_text")
    private List<String> dbSchemaText;

    @JsonProperty("vdb_list")
    private List<String> vdbList;

    @JsonProperty("tag_list")
    private List<String> tagList;

    @JsonProperty("data_usage_errors")
    private List<Map<String, Object>> dataUsageErrors;

    public Map<String, Object> getDbSchemaJson() { return dbSchemaJson; }
    public void setDbSchemaJson(Map<String, Object> dbSchemaJson) { this.dbSchemaJson = dbSchemaJson; }

    public List<String> getDbSchemaText() { return dbSchemaText; }
    public void setDbSchemaText(List<String> dbSchemaText) { this.dbSchemaText = dbSchemaText; }

    public List<String> getVdbList() { return vdbList; }
    public void setVdbList(List<String> vdbList) { this.vdbList = vdbList; }

    public List<String> getTagList() { return tagList; }
    public void setTagList(List<String> tagList) { this.tagList = tagList; }

    public List<Map<String, Object>> getDataUsageErrors() { return dataUsageErrors; }
    public void setDataUsageErrors(List<Map<String, Object>> dataUsageErrors) { this.dataUsageErrors = dataUsageErrors; }
}