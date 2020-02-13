package com.dataguise.saas.dto;

import com.google.gson.Gson;

import java.util.Objects;

public class SourceTreeViewUIFormatterDTO {

    String id;
    String name;
    String parentId;
    Boolean isSensitive ;
    Boolean isCleaned;
    Boolean isProtected;
    Boolean isUnscanned;
    Boolean isUnprocessed;
    Long rowsScanned;
    Long totalRows;
    Object details;
    String agentType;

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public Boolean getSensitive() {
        return isSensitive;
    }

    public void setSensitive(Boolean sensitive) {
        isSensitive = sensitive;
    }

    public Boolean getCleaned() {
        return isCleaned;
    }

    public void setCleaned(Boolean cleaned) {
        isCleaned = cleaned;
    }

    public Boolean getProtected() {
        return isProtected;
    }

    public void setProtected(Boolean aProtected) {
        isProtected = aProtected;
    }

    public Boolean getUnscanned() {
        return isUnscanned;
    }

    public void setUnscanned(Boolean unscanned) {
        isUnscanned = unscanned;
    }

    public Boolean getUnprocessed() {
        return isUnprocessed;
    }

    public void setUnprocessed(Boolean unprocessed) {
        isUnprocessed = unprocessed;
    }

    public Long getRowsScanned() {
        return rowsScanned;
    }

    public void setRowsScanned(Long rowsScanned) {
        this.rowsScanned = rowsScanned;
    }

    public Long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceTreeViewUIFormatterDTO)) return false;
        SourceTreeViewUIFormatterDTO that = (SourceTreeViewUIFormatterDTO) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getParentId(), that.getParentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParentId());
    }

}