package com.dataguise.saas.exception;

import org.springframework.http.HttpStatus;

/**
 * This exceptions are thrown when there is an error in processing from saas-ui backend to frontend.
 */
public class InternalServerException extends Exception {

    String errorMessage;
    HttpStatus status;

    public InternalServerException() {}

    public InternalServerException(String message, HttpStatus status) {
        super(message);
        this.errorMessage = message;
        this.status = status;
    }

    public InternalServerException(String message) {
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
