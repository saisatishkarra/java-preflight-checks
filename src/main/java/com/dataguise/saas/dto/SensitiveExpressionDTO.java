package com.dataguise.saas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensitiveExpressionDTO {

    private Integer regexId;
    private String regexLabel;
    private String regexGroup;

    @JsonProperty("regexId")
    public Integer getRegexId() {
        return regexId;
    }

    @JsonProperty("regexLabel")
    public String getRegexLabel() {
        return regexLabel;
    }

    @JsonProperty("regexGroup")
    public String getRegexGroup() {
        return regexGroup;
    }

    @JsonProperty("recordId")
    public void setRegexId(Integer regexId) {
        this.regexId = regexId;
    }

    @JsonProperty("label")
    public void setRegexLabel(String regexLabel) {
        this.regexLabel = regexLabel;
    }

    @JsonProperty("regexGroup")
    public void setRegexGroup(String regexGroup) {
        this.regexGroup = regexGroup;
    }
}
