package com.dataguise.saas.source;

import com.dataguise.saas.dto.DgAgentClusterDTO;
import io.swagger.client.model.DgConnection;

public class SourceDTO {
    private Boolean autoSchedule;
    private String agentName;
    private String agentHostName;
    private DgConnection connection;
    private DgAgentClusterDTO cluster;
    private String sourceType;
    private String sourceModule;

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

    public Boolean getAutoSchedule() {
        return autoSchedule;
    }

    public void setAutoSchedule(Boolean autoSchedule) {
        this.autoSchedule = autoSchedule;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentHostName() {
        return agentHostName;
    }


    public void setAgentHostName(String agentHostName) {
        this.agentHostName = agentHostName;
    }

    public DgConnection getConnection() {
        return connection;
    }

    public void setConnection(DgConnection connection) {
        this.connection = connection;
    }

    public DgAgentClusterDTO getCluster() {
        return cluster;
    }

    public void setCluster(DgAgentClusterDTO cluster) {
        this.cluster = cluster;
    }
}
