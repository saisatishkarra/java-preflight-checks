package com.dataguise.saas.dto;

public class DgSecureDetails {

    String host;
    Integer port;
    String protocol;
    Boolean secure;
    String dgsecureURL;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public String getDgsecureURL() {
        return dgsecureURL;
    }

    public void setDgsecureURL(String dgsecureURL) {
        this.dgsecureURL = dgsecureURL;
    }
}
