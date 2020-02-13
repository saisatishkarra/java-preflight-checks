package com.dataguise.saas.dto;

public class ProtectionColumnUIStruct {

    private String columnName;
    private String tableName;
    private Integer regexId;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getRegexId() {
        return regexId;
    }

    public void setRegexId(Integer regexId) {
        this.regexId = regexId;
    }
}
