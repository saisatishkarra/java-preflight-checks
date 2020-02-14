package com.dataguise.saas.dto;

import java.util.List;

public class TaskParamsDTO {

    Integer sourceId;
    String sourceName;
    List<ProtectionTaskParamsDTO> protectionTaskParamsDTO;

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public List<ProtectionTaskParamsDTO> getProtectionTaskParamsDTO() {
        return protectionTaskParamsDTO;
    }

    public void setProtectionTaskParamsDTO(List<ProtectionTaskParamsDTO> protectionTaskParamsDTO) {
        this.protectionTaskParamsDTO = protectionTaskParamsDTO;
    }

    public String getSourceName() { return sourceName; }

    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
}
