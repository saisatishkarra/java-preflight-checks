package com.dataguise.saas.util;

import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import io.prometheus.client.CollectorRegistry;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "dgsecure")
@PropertySource("classpath:application.properties")
public class AppConfig {

    private static Logger logger = Logger.getLogger(AppConfig.class);

    String host;

    Integer port;

    String username;

    String password;

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Autowired
    CollectorRegistry collectorRegistry;

    @Override
    public String toString() {
        return "AppConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @PostConstruct
    public void init() {
        try {
            Utility.runHealthChecks(host, port, username, password);
            logger.debug("DgSecure Health check passed");
        } catch (InterruptedException e) {
            logger.warn("DgSecure Health Check failed during initialization: " +e.getMessage(), e);
        }
    }


    @Bean
    @Scope
    public DgSecureRestDriver dgSecureRestDriver() {
        logger.debug("Initializing DgSecureRestDriver");
        DgSecureRestDriver dgSecureRestDriver = DgSecureRestDriver.getInstance(host, port, false, "v1");
        dgSecureRestDriver.setCollectorRegistry(collectorRegistry);
        return dgSecureRestDriver;
    }

}
