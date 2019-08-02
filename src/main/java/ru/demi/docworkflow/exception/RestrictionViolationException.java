package ru.demi.docworkflow.exception;

import java.util.List;

public class RestrictionViolationException extends RuntimeException {
    public RestrictionViolationException() {
    }

    public RestrictionViolationException(List<String> messages) {
        super(messages.toString());
    }
}
