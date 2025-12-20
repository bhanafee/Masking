package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for an Employer Identification number.
 * This class is final to prevent subclasses from undermining the security protections.
 */
public final class EIN extends UsTIN {
    public static final String EIN_REGEX = "(?<campus>\\d{2})-(?<serial>\\d{7})";

    private static final Pattern EIN_PATTERN = Pattern.compile(EIN_REGEX);

    public static String[] parse(final CharSequence value) {
        if (value == null) {
            throw new InvalidTINException("EIN value cannot be null");
        }
        final Matcher matcher = EIN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("campus"),
                    matcher.group("serial")
            };
        } else {
            throw new InvalidTINException("Invalid EIN format: expected ##-####### (length: " + value.length() + ")");
        }
    }

    public EIN(String prefix, String serial) {
        super(validateSegments(prefix, serial));
    }

    private static String[] validateSegments(final String prefix, final String serial) {
        if (prefix == null || serial == null) {
            throw new InvalidTINException("EIN segments cannot be null");
        }
        if (!prefix.matches("\\d{2}")) {
            throw new InvalidTINException("Invalid EIN campus: expected 2 digits (length: " + prefix.length() + ")");
        }
        if (!serial.matches("\\d{7}")) {
            throw new InvalidTINException("Invalid EIN serial: expected 7 digits (length: " + serial.length() + ")");
        }
        return new String[]{prefix, serial};
    }

    public EIN(int prefix, int serial) {
        this(
                validateIntSegment(prefix, 99, "EIN", "campus"),
                validateIntSegment(serial, 9999999, "EIN", "serial")
        );
    }

    public EIN(final CharSequence value) {
        super(parse(value));
    }

    public CharSequence getPrefix() {
        return getContained()[0];
    }

    public CharSequence getSerial() {
        return getContained()[1];
    }
}
