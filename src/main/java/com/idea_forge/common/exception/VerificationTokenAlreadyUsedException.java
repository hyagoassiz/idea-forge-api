package com.idea_forge.common.exception;

public class VerificationTokenAlreadyUsedException extends RuntimeException {
    public VerificationTokenAlreadyUsedException(String message) {
        super(message);
    }
}
