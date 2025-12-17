package com.maybeitssquid.tin.us;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for an Employer Identification number.
 * This class is final to prevent subclasses from undermining the security protections.
 */
public final class EIN extends TIN {
    public static final String EIN_REGEX = "(?<prefix>\\d{2})-(?<serial>\\d{7})";

    private static final Pattern EIN_PATTERN = Pattern.compile(EIN_REGEX);

    public static String[] parse(final CharSequence value) {
        if (value == null) {
            throw new InvalidTINException("EIN value cannot be null");
        }
        final Matcher matcher = EIN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("prefix"),
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
            throw new InvalidTINException("Invalid EIN prefix: expected 2 digits (length: " + prefix.length() + ")");
        }
        if (!serial.matches("\\d{7}")) {
            throw new InvalidTINException("Invalid EIN serial: expected 7 digits (length: " + serial.length() + ")");
        }
        return new String[]{prefix, serial};
    }

    public EIN(int prefix, int serial) {
        this(
                validateIntSegment(prefix, 0, 99, "prefix", 2),
                validateIntSegment(serial, 0, 9999999, "serial", 7)
        );
    }

    private static String validateIntSegment(final int value, final int min, final int max, final String name, final int width) {
        if (value < min || value > max) {
            throw new InvalidTINException(
                    String.format("Invalid EIN %s: expected range %d-%d", name, min, max)
            );
        }
        return String.format(Locale.US, "%0" + width + "d", value);
    }

    public EIN(final CharSequence value) {
        super(parse(value));
    }

    public String getPrefix() {
        return getContained()[0];
    }

    public String getSerial() {
        return getContained()[1];
    }
}
