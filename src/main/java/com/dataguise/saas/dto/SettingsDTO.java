package com.dataguise.saas.dto;

import java.util.List;

public class SettingsDTO {

    List<String> policySelected = null;

    String dgsecureClassicURL = null;

    public List<String> getPolicySelected() {
        return policySelected;
    }

    public void setPolicySelected(List<String> policySelected) {
        this.policySelected = policySelected;
    }

    public String getDgsecureClassicURL() {
        return dgsecureClassicURL;
    }

    public void setDgsecureClassicURL(String dgsecureClassicURL) {
        this.dgsecureClassicURL = dgsecureClassicURL;
    }

    public void setPolicyType(List<String> policyType) {
        this.policySelected = policyType;
    }

}
