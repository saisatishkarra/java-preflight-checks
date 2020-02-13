package com.dataguise.saas.dto;

public class SourceCountDTO {

    private Integer cloud_sources;
    private Integer on_premise_sources;

    public Integer getCloud_sources() {
        return cloud_sources;
    }

    public void setCloud_sources(Integer cloud_sources) {
        this.cloud_sources = cloud_sources;
    }

    public Integer getOn_premise_sources() {
        return on_premise_sources;
    }

    public void setOn_premise_sources(Integer on_premise_sources) {
        this.on_premise_sources = on_premise_sources;
    }
}
