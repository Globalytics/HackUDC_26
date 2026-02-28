package com.decisiontool.dto;

import jakarta.validation.constraints.NotBlank;

public class UploadDatasetRequest {

    @NotBlank(message = "El nombre del dataset no puede estar vacío")
    private String name;
    private String description;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String content;

    private String format;

    public UploadDatasetRequest() {}

    public UploadDatasetRequest(String name, String description, String content, String format) {
        this.name = name; this.description = description; this.content = content; this.format = format;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name, description, content, format;
        public Builder name(String v)        { this.name = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder content(String v)     { this.content = v; return this; }
        public Builder format(String v)      { this.format = v; return this; }
        public UploadDatasetRequest build() { return new UploadDatasetRequest(name, description, content, format); }
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getContent()     { return content; }
    public String getFormat()      { return format; }
    public void setName(String v)        { this.name = v; }
    public void setDescription(String v) { this.description = v; }
    public void setContent(String v)     { this.content = v; }
    public void setFormat(String v)      { this.format = v; }
}
