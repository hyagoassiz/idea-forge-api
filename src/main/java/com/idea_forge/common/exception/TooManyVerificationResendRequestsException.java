package com.idea_forge.common.exception;

public class TooManyVerificationResendRequestsException extends RuntimeException {
    public TooManyVerificationResendRequestsException(String message) {
        super(message);
    }
}
