package com.decisiontool.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Dataset {

    private String id;
    private String name;
    private String description;
    private String source;
    private List<DatasetColumn> columns;
    private List<Map<String, Object>> rows;
    private DatasetStats stats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Dataset() {}

    public Dataset(String id, String name, String description, String source,
                   List<DatasetColumn> columns, List<Map<String, Object>> rows,
                   DatasetStats stats, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.name = name; this.description = description; this.source = source;
        this.columns = columns; this.rows = rows; this.stats = stats;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, name, description, source;
        private List<DatasetColumn> columns;
        private List<Map<String, Object>> rows;
        private DatasetStats stats;
        private LocalDateTime createdAt, updatedAt;
        public Builder id(String v)                          { this.id = v; return this; }
        public Builder name(String v)                        { this.name = v; return this; }
        public Builder description(String v)                 { this.description = v; return this; }
        public Builder source(String v)                      { this.source = v; return this; }
        public Builder columns(List<DatasetColumn> v)        { this.columns = v; return this; }
        public Builder rows(List<Map<String, Object>> v)     { this.rows = v; return this; }
        public Builder stats(DatasetStats v)                 { this.stats = v; return this; }
        public Builder createdAt(LocalDateTime v)            { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v)            { this.updatedAt = v; return this; }
        public Dataset build() {
            return new Dataset(id, name, description, source, columns, rows, stats, createdAt, updatedAt);
        }
    }

    public String getId()                           { return id; }
    public String getName()                         { return name; }
    public String getDescription()                  { return description; }
    public String getSource()                       { return source; }
    public List<DatasetColumn> getColumns()         { return columns; }
    public List<Map<String, Object>> getRows()      { return rows; }
    public DatasetStats getStats()                  { return stats; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setId(String v)                     { this.id = v; }
    public void setName(String v)                   { this.name = v; }
    public void setDescription(String v)            { this.description = v; }
    public void setSource(String v)                 { this.source = v; }
    public void setColumns(List<DatasetColumn> v)   { this.columns = v; }
    public void setRows(List<Map<String, Object>> v){ this.rows = v; }
    public void setStats(DatasetStats v)            { this.stats = v; }
    public void setCreatedAt(LocalDateTime v)       { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v)       { this.updatedAt = v; }
}
