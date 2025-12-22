package com.maybeitssquid.tin.us;

import java.util.regex.Pattern;

/**
 * Container for an Employer Identification number.
 * This class is final to prevent subclasses from undermining the security protections.
 */
public final class EIN extends UsTIN {
    public static final Segment CAMPUS = new Segment("campus", 2);
    public static final Segment SERIAL = new Segment("serial", 7);

    private static final Segment[] EXPECTED = { CAMPUS, SERIAL };

    private static final Pattern EIN_PATTERN = Pattern.compile(
            CAMPUS.regex() + "-" + SERIAL.regex()
    );

    public EIN(String campus, String serial) {
        super(validateSegments(EXPECTED, campus, serial));
    }

    public EIN(int campus, int serial) {
        super(validateIntSegments(EXPECTED, campus, serial));
    }

    public EIN(final CharSequence value) {
        super(parse(EIN_PATTERN, value));
    }

    public String getCampus() {
        final CharSequence campus = getValue(0);
        return campus == null ? "" : campus.toString();
    }

    public String getSerial() {
        final CharSequence serial = getValue(1);
        return serial == null ? "" : serial.toString();
    }
}
