package com.iot.relay.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class
 * 
 * @author Ananthan periyasamy
 */

public class SensorCustomException extends RuntimeException {

    private final HttpStatus status;

    public SensorCustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
