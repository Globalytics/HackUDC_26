package com.decisiontool.dto;

import java.util.List;

public class DenodoViewsResponse {
    private List<Element> elements;
    private Integer elementsCount;

    public List<Element> getElements() { return elements; }
    public Integer getElementsCount() { return elementsCount; }

    public void setElements(List<Element> elements) { this.elements = elements; }
    public void setElementsCount(Integer elementsCount) { this.elementsCount = elementsCount; }

    public static class Element {
        private Integer id;
        private String name;
        private Database database;

        public Integer getId() { return id; }
        public String getName() { return name; }
        public Database getDatabase() { return database; }

        public void setId(Integer id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setDatabase(Database database) { this.database = database; }
    }

    public static class Database {
        private Integer databaseId;
        private String databaseName;

        public Integer getDatabaseId() { return databaseId; }
        public String getDatabaseName() { return databaseName; }

        public void setDatabaseId(Integer databaseId) { this.databaseId = databaseId; }
        public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    }
}