package com.decisiontool.model;

public class DatasetColumn {

    private String name;
    private String type;
    private String description;
    private boolean numeric;
    private boolean nullable;

    public DatasetColumn() {}

    public DatasetColumn(String name, String type, String description, boolean numeric, boolean nullable) {
        this.name = name; this.type = type; this.description = description;
        this.numeric = numeric; this.nullable = nullable;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name, type, description;
        private boolean numeric, nullable;
        public Builder name(String v)        { this.name = v; return this; }
        public Builder type(String v)        { this.type = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder numeric(boolean v)    { this.numeric = v; return this; }
        public Builder nullable(boolean v)   { this.nullable = v; return this; }
        public DatasetColumn build() { return new DatasetColumn(name, type, description, numeric, nullable); }
    }

    public String getName()        { return name; }
    public String getType()        { return type; }
    public String getDescription() { return description; }
    public boolean isNumeric()     { return numeric; }
    public boolean isNullable()    { return nullable; }
    public void setName(String v)        { this.name = v; }
    public void setType(String v)        { this.type = v; }
    public void setDescription(String v) { this.description = v; }
    public void setNumeric(boolean v)    { this.numeric = v; }
    public void setNullable(boolean v)   { this.nullable = v; }
}
