package com.maybeitssquid.tin.us;

import java.util.regex.Pattern;

/**
 * Container for an SSN or ITIN.
 * This class is final to prevent subclasses from undermining the security protections.
 */
public final class SSN extends UsTIN {
    public static final Segment AREA = new Segment("area", 3);
    public static final Segment GROUP = new Segment("group", 2);
    public static final Segment SERIAL = new Segment("serial", 4);

    private static final Segment[] EXPECTED = { AREA, GROUP, SERIAL };

    private static final Pattern SSN_PATTERN = Pattern.compile(
            AREA.regex() + "-" + GROUP.regex() + "-" + SERIAL.regex()
    );

    public SSN(final String area, final String group, final String serial) {
        super(validateSegments(EXPECTED, area, group, serial));
    }

    public SSN(final int area, final int group, final int serial) {
        super(validateIntSegments(EXPECTED, area, group, serial));
    }

    public SSN(final CharSequence value) {
        super(parse(SSN_PATTERN, value));
    }

    public String getArea() {
        final CharSequence area = getValue(0);
        return area == null ? "" : area.toString();
    }

    public String getGroup() {
        final CharSequence group = getValue(1);
        return group == null ? "" : group.toString();
    }

    public String getSerial() {
        final CharSequence serial = getValue(2);
        return serial == null ? "" : serial.toString();
    }
}
