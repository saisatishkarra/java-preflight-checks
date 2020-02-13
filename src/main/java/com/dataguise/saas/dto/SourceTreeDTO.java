package com.dataguise.saas.dto;

public class SourceTreeDTO {
    private String text;
    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
