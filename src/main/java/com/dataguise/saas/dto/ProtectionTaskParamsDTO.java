package com.dataguise.saas.dto;

import java.util.List;

public class ProtectionTaskParamsDTO {

    String databaseName;
    List<ProtectionColumnUIStruct> columnStructure;


    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<ProtectionColumnUIStruct> getColumnStructure() {
        return columnStructure;
    }

    public void setColumnStructure(List<ProtectionColumnUIStruct> columnStructure) {
        this.columnStructure = columnStructure;
    }
}
