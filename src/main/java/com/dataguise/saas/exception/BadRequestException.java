package com.dataguise.saas.exception;

import org.springframework.http.HttpStatus;

/**
 * This exceptions are thrown when the requests to saas-backed from UI is not as expected.
 */
public class BadRequestException extends Exception {

    String errorMessage;
    HttpStatus status;

    public BadRequestException() {}

    public BadRequestException(String message, HttpStatus status) {
        super(message);
        this.errorMessage = message;
        this.status = status;
    }

    public BadRequestException(String message) {
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
