package com.maybeitssquid.tin.us;

/**
 * Exception thrown when a TIN (SSN or EIN) cannot be parsed or is invalid.
 */
public class InvalidTINException extends IllegalArgumentException {

    public InvalidTINException(String message) {
        super(message);
    }

    public InvalidTINException(String message, Throwable cause) {
        super(message, cause);
    }
}
