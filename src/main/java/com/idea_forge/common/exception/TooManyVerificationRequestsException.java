package com.idea_forge.common.exception;

public class TooManyVerificationRequestsException extends RuntimeException {
    public TooManyVerificationRequestsException(String message) {
        super(message);
    }
}
