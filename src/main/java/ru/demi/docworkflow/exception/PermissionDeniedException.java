package ru.demi.docworkflow.exception;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
    }

    public PermissionDeniedException(String message) {
        super(message);
    }
}
