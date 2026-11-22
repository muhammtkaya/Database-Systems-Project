package com.etu.expensemanagement.exception;

import org.springframework.http.HttpStatus;

/** Base class for errors we throw on purpose. Each one maps to an HTTP status. */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
