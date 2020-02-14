package com.dataguise.saas.dto;

import com.google.gson.Gson;

public class AboutDTO {
    String driverConfig;
    String revision;

    public String getDriverConfig() {
        return driverConfig;
    }

    public void setDriverConfig(String driverConfig) {
        this.driverConfig = driverConfig;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
