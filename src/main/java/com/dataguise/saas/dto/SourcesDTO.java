package com.dataguise.saas.dto;

import com.google.gson.Gson;

public class SourcesDTO {

    String name;
    String sourceType;
    String serverType;
    String sourceName;
    Integer baseType;
    Integer sourceId;
    Integer cleaned;
    Integer exposed;
    Integer protectedData;
    Integer unscanned;
    String agentType;

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getBaseType() {
        return baseType;
    }

    public void setBaseType(Integer baseType) {
        this.baseType = baseType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getCleaned() {
        return cleaned;
    }

    public void setCleaned(Integer cleaned) {
        this.cleaned = cleaned;
    }

    public Integer getExposed() {
        return exposed;
    }

    public void setExposed(Integer exposed) {
        this.exposed = exposed;
    }

    public Integer getProtectedData() {
        return protectedData;
    }

    public void setProtectedData(Integer protectedData) {
        this.protectedData = protectedData;
    }

    public Integer getUnscanned() {
        return unscanned;
    }

    public void setUnscanned(Integer unscanned) {
        this.unscanned = unscanned;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
