package com.idea_forge.common.exception;

public class EmailVerificationSendFailedException extends RuntimeException {
    public EmailVerificationSendFailedException(String message) {
        super(message);
    }
}
