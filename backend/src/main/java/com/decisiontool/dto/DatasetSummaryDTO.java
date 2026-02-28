package com.decisiontool.dto;

import java.time.LocalDateTime;

public class DatasetSummaryDTO {

    private String id;
    private String name;
    private String description;
    private String source;
    private int rowCount;
    private int columnCount;
    private LocalDateTime createdAt;

    public DatasetSummaryDTO() {}

    public DatasetSummaryDTO(String id, String name, String description, String source,
                             int rowCount, int columnCount, LocalDateTime createdAt) {
        this.id = id; this.name = name; this.description = description; this.source = source;
        this.rowCount = rowCount; this.columnCount = columnCount; this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, name, description, source;
        private int rowCount, columnCount;
        private LocalDateTime createdAt;

        public Builder id(String v)            { this.id = v; return this; }
        public Builder name(String v)          { this.name = v; return this; }
        public Builder description(String v)   { this.description = v; return this; }
        public Builder source(String v)        { this.source = v; return this; }
        public Builder rowCount(int v)         { this.rowCount = v; return this; }
        public Builder columnCount(int v)      { this.columnCount = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public DatasetSummaryDTO build() {
            return new DatasetSummaryDTO(id, name, description, source, rowCount, columnCount, createdAt);
        }
    }

    public String getId()              { return id; }
    public String getName()            { return name; }
    public String getDescription()     { return description; }
    public String getSource()          { return source; }
    public int getRowCount()           { return rowCount; }
    public int getColumnCount()        { return columnCount; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public void setId(String v)              { this.id = v; }
    public void setName(String v)            { this.name = v; }
    public void setDescription(String v)     { this.description = v; }
    public void setSource(String v)          { this.source = v; }
    public void setRowCount(int v)           { this.rowCount = v; }
    public void setColumnCount(int v)        { this.columnCount = v; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }
}
