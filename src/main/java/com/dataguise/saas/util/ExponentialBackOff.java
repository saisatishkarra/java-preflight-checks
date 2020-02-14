package com.dataguise.saas.util;

import java.util.Random;

public class ExponentialBackOff {

    //public static final int DEFAULT_RETRIES = 3;

    public static final long INITIAL_INTERVAL_IN_MILLI=8000;
    public static final long MAX_ELAPSED_TIME_IN_MILLI=240000;
    public static final long MULTIPLIER = 8;

    private long maxElapsedTime;
    private long elapsedTime;

    private Random random = new Random();

    public ExponentialBackOff() {
        this(INITIAL_INTERVAL_IN_MILLI, MAX_ELAPSED_TIME_IN_MILLI);
    }

    public ExponentialBackOff(long initialInterval, long maxElapsedTime) {
        this.elapsedTime = initialInterval;
        this.maxElapsedTime = maxElapsedTime;
    }


    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @return true if there are tries left
     */
    public boolean shouldRetry() {
        return elapsedTime < maxElapsedTime;
    }


    public void updateExponentially(long elapsedTime) throws InterruptedException {
        Thread.sleep(elapsedTime);
        elapsedTime *= MULTIPLIER;
        this.elapsedTime = elapsedTime;
    }

}