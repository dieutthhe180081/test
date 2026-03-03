package com.sep490.g28.hvh.be.notification.exception;

public class NonRetryableFcmException extends RuntimeException {
    public NonRetryableFcmException(String message) {
        super(message);
    }
}
