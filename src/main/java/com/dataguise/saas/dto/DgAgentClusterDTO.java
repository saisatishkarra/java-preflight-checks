package com.dataguise.saas.dto;

public class DgAgentClusterDTO {
    Integer clusterAgentId;
    Integer cloudAgentId;
    Integer clusterId;
    String clusterName;

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getClusterAgentId() {
        return clusterAgentId;
    }

    public void setClusterAgentId(Integer clusterAgentId) {
        this.clusterAgentId = clusterAgentId;
    }

    public Integer getCloudAgentId() {
        return cloudAgentId;
    }

    public void setCloudAgentId(Integer cloudAgentId) {
        this.cloudAgentId = cloudAgentId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
