package com.dataguise.saas.dto;

import com.google.gson.Gson;

public class ColumnViewUIFormatterDTO {

    String columnName;
    String dataType;
    Integer regexId;
    String regexGroup;
    String regexLabel;
    Boolean isSensitive;
    Boolean isProtected;
    Boolean isUnprocessed;
    Boolean isUnscanned;
    Boolean isColumnHeaderMatched;
    Boolean isCleaned;
    Boolean isSafe;
    Long hitCount;
    Long mismatchCount;
    Long nullCount;
    Double expressionGroupConfidence;



    String constraintType;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getRegexId() {
        return regexId;
    }

    public void setRegexId(Integer regexId) {
        this.regexId = regexId;
    }

    public String getRegexGroup() {
        return regexGroup;
    }

    public void setRegexGroup(String regexGroup) {
        this.regexGroup = regexGroup;
    }

    public String getRegexLabel() {
        return regexLabel;
    }

    public void setRegexLabel(String regexLabel) {
        this.regexLabel = regexLabel;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    public Boolean getIsSensitive() {
        return isSensitive;
    }

    public void setIsSensitive(Boolean sensitive) {
        isSensitive = sensitive;
    }

    public Boolean getSensitive() {
        return isSensitive;
    }

    public void setSensitive(Boolean sensitive) {
        isSensitive = sensitive;
    }

    public Boolean getProtected() {
        return isProtected;
    }

    public void setProtected(Boolean aProtected) {
        isProtected = aProtected;
    }

    public Boolean getUnprocessed() {
        return isUnprocessed;
    }

    public void setUnprocessed(Boolean unprocessed) {
        isUnprocessed = unprocessed;
    }

    public Boolean getUnscanned() {
        return isUnscanned;
    }

    public void setUnscanned(Boolean unscanned) {
        isUnscanned = unscanned;
    }

    public Boolean getColumnHeaderMatched() {
        return isColumnHeaderMatched;
    }

    public void setColumnHeaderMatched(Boolean columnHeaderMatched) {
        isColumnHeaderMatched = columnHeaderMatched;
    }

    public Boolean getCleaned() {
        return isCleaned;
    }

    public void setCleaned(Boolean cleaned) {
        isCleaned = cleaned;
    }

    public Boolean getSafe() {
        return isSafe;
    }

    public void setSafe(Boolean safe) {
        isSafe = safe;
    }

    public Long getHitCount() {
        return hitCount;
    }

    public void setHitCount(Long hitCount) {
        this.hitCount = hitCount;
    }

    public Long getMismatchCount() {
        return mismatchCount;
    }

    public void setMismatchCount(Long mismatchCount) {
        this.mismatchCount = mismatchCount;
    }

    public Long getNullCount() {
        return nullCount;
    }

    public void setNullCount(Long nullCount) {
        this.nullCount = nullCount;
    }

    public Double getExpressionGroupConfidence() {
        return expressionGroupConfidence;
    }

    public void setExpressionGroupConfidence(Double expressionGroupConfidence) {
        this.expressionGroupConfidence = expressionGroupConfidence;
    }

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }
}
