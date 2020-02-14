package com.dataguise.saas.dto;

import com.google.gson.Gson;
import io.swagger.client.model.DgConnection;

public class ConnectionDTO {

    DgConnection connection;
    Boolean autoSchedule;
    String agentName;
    String agentHostName;

    public DgConnection getConnection() {
        return connection;
    }

    public void setConnection(DgConnection connection) {
        this.connection = connection;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
