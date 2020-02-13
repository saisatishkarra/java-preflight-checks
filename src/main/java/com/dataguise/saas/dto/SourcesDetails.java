package com.dataguise.saas.dto;

import java.sql.Timestamp;
import java.util.List;

public class SourcesDetails {
    private int sourceId;
    private String sourceName;
    private String sourceType;
    private String sourceModule;
    private String sourceAgent;
    private String sourceHostName;
    private Integer sourcePortNumber;
    private List<String> sourceDatabases;
    private List<String> sourceSchemas;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    private Timestamp createdAt;
    public String getSourceHostName() {
        return sourceHostName;
    }

    public void setSourceHostName(String sourceHostName) {
        this.sourceHostName = sourceHostName;
    }

    public Integer getSourcePortNumber() {
        return sourcePortNumber;
    }

    public void setSourcePortNumber(Integer sourcePortNumber) {
        this.sourcePortNumber = sourcePortNumber;
    }

    public List<String> getSourceDatabases() {
        return sourceDatabases;
    }

    public void setSourceDatabases(List<String> sourceDatabases) {
        this.sourceDatabases = sourceDatabases;
    }

    public List<String> getSourceSchemas() {
        return sourceSchemas;
    }

    public void setSourceSchemas(List<String> sourceSchemas) {
        this.sourceSchemas = sourceSchemas;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceModule() {
        return sourceModule;
    }

    public void setSourceModule(String sourceModule) {
        this.sourceModule = sourceModule;
    }

    public String getSourceAgent() {
        return sourceAgent;
    }

    public void setSourceAgent(String sourceAgent) {
        this.sourceAgent = sourceAgent;
    }
}
