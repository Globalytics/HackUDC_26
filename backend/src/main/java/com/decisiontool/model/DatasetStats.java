package com.decisiontool.model;

import java.util.Map;

public class DatasetStats {

    private int rowCount;
    private int columnCount;
    private Map<String, Double> means;
    private Map<String, Double> mins;
    private Map<String, Double> maxs;
    private Map<String, Double> stdDevs;

    public DatasetStats() {}

    public DatasetStats(int rowCount, int columnCount,
                        Map<String, Double> means, Map<String, Double> mins,
                        Map<String, Double> maxs, Map<String, Double> stdDevs) {
        this.rowCount = rowCount; this.columnCount = columnCount;
        this.means = means; this.mins = mins; this.maxs = maxs; this.stdDevs = stdDevs;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int rowCount, columnCount;
        private Map<String, Double> means, mins, maxs, stdDevs;
        public Builder rowCount(int v)                  { this.rowCount = v; return this; }
        public Builder columnCount(int v)               { this.columnCount = v; return this; }
        public Builder means(Map<String, Double> v)     { this.means = v; return this; }
        public Builder mins(Map<String, Double> v)      { this.mins = v; return this; }
        public Builder maxs(Map<String, Double> v)      { this.maxs = v; return this; }
        public Builder stdDevs(Map<String, Double> v)   { this.stdDevs = v; return this; }
        public DatasetStats build() { return new DatasetStats(rowCount, columnCount, means, mins, maxs, stdDevs); }
    }

    public int getRowCount()                    { return rowCount; }
    public int getColumnCount()                 { return columnCount; }
    public Map<String, Double> getMeans()       { return means; }
    public Map<String, Double> getMins()        { return mins; }
    public Map<String, Double> getMaxs()        { return maxs; }
    public Map<String, Double> getStdDevs()     { return stdDevs; }
    public void setRowCount(int v)              { this.rowCount = v; }
    public void setColumnCount(int v)           { this.columnCount = v; }
    public void setMeans(Map<String, Double> v) { this.means = v; }
    public void setMins(Map<String, Double> v)  { this.mins = v; }
    public void setMaxs(Map<String, Double> v)  { this.maxs = v; }
    public void setStdDevs(Map<String, Double> v){ this.stdDevs = v; }
}
