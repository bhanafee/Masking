package com.maybeitssquid.tin.us;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for an SSN or ITIN.
 */
public class SSN extends TIN {

    public static final String SSN_REGEX = "(?<area>\\d{3})-(?<group>\\d{2})-(?<serial>\\d{4})";

    private static final Pattern SSN_PATTERN = Pattern.compile(SSN_REGEX);

    public static String[] parse(final CharSequence value) {
        if (value == null) {
            throw new InvalidTINException("SSN value cannot be null");
        }
        final Matcher matcher = SSN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("area"),
                    matcher.group("group"),
                    matcher.group("serial")
            };
        } else {
            throw new InvalidTINException("Invalid SSN format: expected ###-##-#### (length: " + value.length() + ")");
        }
    }

    public SSN(final String area, final String group, final String serial) {
        super(validateSegments(area, group, serial));
    }

    private static String[] validateSegments(final String area, final String group, final String serial) {
        if (area == null || group == null || serial == null) {
            throw new InvalidTINException("SSN segments cannot be null");
        }
        if (!area.matches("\\d{3}")) {
            throw new InvalidTINException("Invalid SSN area number: expected 3 digits (length: " + area.length() + ")");
        }
        if (!group.matches("\\d{2}")) {
            throw new InvalidTINException("Invalid SSN group number: expected 2 digits (length: " + group.length() + ")");
        }
        if (!serial.matches("\\d{4}")) {
            throw new InvalidTINException("Invalid SSN serial number: expected 4 digits (length: " + serial.length() + ")");
        }
        return new String[]{area, group, serial};
    }

    public SSN(final int area, final int group, final int serial) {
        this(
                validateIntSegment(area, 0, 999, "area"),
                validateIntSegment(group, 0, 99, "group"),
                validateIntSegment(serial, 0, 9999, "serial")
        );
    }

    private static String validateIntSegment(final int value, final int min, final int max, final String name) {
        if (value < min || value > max) {
            throw new InvalidTINException(
                    String.format("Invalid SSN %s: expected range %d-%d", name, min, max)
            );
        }
        final int width = String.valueOf(max).length();
        return String.format(Locale.US, "%0" + width + "d", value);
    }

    public SSN(final CharSequence value) {
        super(parse(value));
    }

    public String getArea() {
        return getContained()[0];
    }

    public String getGroup() {
        return getContained()[1];
    }

    public String getSerial() {
        return getContained()[2];
    }
}
