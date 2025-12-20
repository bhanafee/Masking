package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for an SSN or ITIN.
 * This class is final to prevent subclasses from undermining the security protections.
 */
public final class SSN extends UsTIN {

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
        } else if ("000".equals(area)) {
            throw new InvalidTINException("Invalid SSN area number: cannot be 000");
        }
        if (!group.matches("\\d{2}")) {
            throw new InvalidTINException("Invalid SSN group number: expected 2 digits (length: " + group.length() + ")");
        } else if ("00".equals(group)) {
            throw new InvalidTINException("Invalid SSN group number: cannot be 00");
        }
        if (!serial.matches("\\d{4}")) {
            throw new InvalidTINException("Invalid SSN serial number: expected 4 digits (length: " + serial.length() + ")");
        } else if ("0000".equals(serial)) {
            throw new InvalidTINException("Invalid SSN serial number: cannot be 0000");
        }
        return new String[]{area, group, serial};
    }

    public SSN(final int area, final int group, final int serial) {
        this(
                validateIntSegment(area, 999, "SSN", "area"),
                validateIntSegment(group, 99, "SSN", "group"),
                validateIntSegment(serial, 9999, "SSN", "serial")
        );
    }

    public SSN(final CharSequence value) {
        super(parse(value));
    }

    public CharSequence getArea() {
        return getContained()[0];
    }

    public CharSequence getGroup() {
        return getContained()[1];
    }

    public CharSequence getSerial() {
        return getContained()[2];
    }
}
