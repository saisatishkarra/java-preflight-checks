package com.dataguise.saas.dto;

public enum PolicyType {
    PCI ("PCI"),
    PII ("PII"),
    HIPAA ("HIPAA"),
    GDPR ("GDPR");

    private String policy;

    private PolicyType(String policy) {
        this.policy = policy;
    }

    public String toString() {
        return this.policy;
    }
}
