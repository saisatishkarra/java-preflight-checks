package com.dataguise.saas.dto;

import java.util.List;

public class ImexDTO {
    String sourceName;
    String reportType;
    List<String> exportedColList;

    public String getSourceName() { return sourceName; }

    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public List<String> getExportedColList() {
        return exportedColList;
    }

    public void setExportedColList(List<String> exportedColList) {
        this.exportedColList = exportedColList;
    }
}
