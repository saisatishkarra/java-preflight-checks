package com.dataguise.saas.actuator;

import com.dataguise.saas.util.AppConfig;
import com.dataguise.saas.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class DgSecureHealthCheck implements HealthIndicator {

    @Autowired
    AppConfig appConfig;

    private String host;
    private Integer port;
    private String username;
    private String password;


    @PostConstruct
    public void init() {
        host = appConfig.getHost();
        port = appConfig.getPort();
        username = appConfig.getUsername();
        password = appConfig.getPassword();

    }

    @Override
    public Health health() {

        Health.Builder builder = Health.up();

        String url = String.format("http://%s:%s/", host, port);
        int responseCode = 0;

        try {

            responseCode = Utility.checkDgSecureServerStatus(url);
            if (200 <= responseCode && responseCode <= 399) {
                builder = builder.withDetail("DgSecure Server Response", String.valueOf(responseCode));
            } else {
                builder = builder.down().withDetail("DgSecure Server Response", String.valueOf(responseCode));
                return builder.build();
            }

            responseCode = Utility.checkDgSecureSetupStatus(url, username, password);

            if (200 <= responseCode && responseCode <= 399) {
                builder.withDetail("DgSecure Login Response", String.valueOf(responseCode));
            } else {
                builder.down().withDetail("DgSecure Login Response", String.valueOf(responseCode));
            }

        } catch (IOException e) {
            return builder.down(e).build();
        }

        return builder.build();

    }

}
