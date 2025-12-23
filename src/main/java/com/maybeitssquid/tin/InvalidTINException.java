package com.maybeitssquid.tin;

/**
 * Exception thrown when a Taxpayer Identification Number cannot be parsed or is invalid.
 *
 * <p>This exception is thrown when:
 * <ul>
 *     <li>A TIN string does not match the expected format</li>
 *     <li>A TIN segment value is outside the valid range</li>
 *     <li>A null value is provided where a TIN is required</li>
 * </ul>
 *
 * @see com.maybeitssquid.tin.us.SSN
 * @see com.maybeitssquid.tin.us.EIN
 */
public class InvalidTINException extends IllegalArgumentException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidTINException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message describing the validation failure
     * @param cause   the cause of this exception
     */
    public InvalidTINException(String message, Throwable cause) {
        super(message, cause);
    }
}
