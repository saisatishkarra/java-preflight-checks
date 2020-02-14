package com.dataguise.saas.exception;

import org.springframework.http.HttpStatus;

/**
 * This exceptions are thrown when there is an error in processing requests to driver/upstream server.
 */
public class BadGatewayException extends Exception{
    String errorMessage;
    HttpStatus status;

    public BadGatewayException() {}

    public BadGatewayException(String message, HttpStatus status) {
        super(message);
        this.errorMessage = message;
        this.status = status;
    }

    public BadGatewayException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
