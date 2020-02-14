package com.dataguise.saas.util;

import com.dg.saas.orch.models.structures.ErrorConstants;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class Utility {
    private static Logger logger = LoggerFactory.getLogger(Utility.class);

    public static void runHealthChecks(String host, Integer port, String username, String password) throws InterruptedException {

        ExponentialBackOff backoff = new ExponentialBackOff();
        Boolean serverStatus = false;
        Boolean dgSecureStatus = false;
        String url = String.format("http://%s:%s/", host, port);
        String loginUrl = String.format("http://%s:%s/dgcontroller/services/v1/sessions",host,port);

        while(backoff.shouldRetry()) {
            try {
                int responseCode = checkDgSecureServerStatus(url);
                if (200 <= responseCode && responseCode <= 399) {
                    serverStatus = true;
                }
            } catch (IOException e) {
                serverStatus = false;
            }

            if(serverStatus == true) {
                try {
                    int responseCode = checkDgSecureSetupStatus(loginUrl, username, password);
                    if (200 <= responseCode && responseCode <= 399) {
                        dgSecureStatus = true;
                    }
                } catch (IOException e) {
                    dgSecureStatus = false;
                }
            }
            if(serverStatus == true && dgSecureStatus == true) {
                break;
            }
            else {
                logger.info("Current backoff: "+backoff.getElapsedTime());
                backoff.updateExponentially(backoff.getElapsedTime());
            }
        }

        if(serverStatus == false) {
            throw new InterruptedException(ErrorConstants.ERROR_CONNECTING_WITH_DGSECURE.getErrorMessage());
        }else if (serverStatus == true && dgSecureStatus == false) {
            throw new InterruptedException(ErrorConstants.LOGIN_FAIL_DGSECURE.getErrorMessage());
        }

    }


    public static int checkDgSecureServerStatus(String url) throws IOException {
        logger.info("Checking server status of DgSecure at " + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        return responseCode;
    }

    public static int checkDgSecureSetupStatus(String url, String username, String password) throws IOException {
        logger.info("Checking DgSecure configuration at " + url);
        JsonObject postData = new JsonObject();
        postData.addProperty("password", password);
        postData.addProperty("userName", username);
        postData.addProperty("source", "");

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json");
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(postData.toString());
        wr.flush();

        int responseCode = connection.getResponseCode();
        return responseCode;
    }


    /**
     * Get Distinct filter result list from the key value of the beans.
     */
    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t -> {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

}

