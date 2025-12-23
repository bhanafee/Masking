package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;

import java.util.regex.Pattern;

/**
 * A Social Security Number (SSN) or Individual Taxpayer Identification Number (ITIN).
 *
 * <p>SSNs are nine-digit numbers in the format {@code AAA-GG-SSSS} where:
 * <ul>
 *     <li>AAA is the area number (3 digits)</li>
 *     <li>GG is the group number (2 digits)</li>
 *     <li>SSSS is the serial number (4 digits)</li>
 * </ul>
 *
 * <p>This class provides automatic masking when formatted. By default, the SSN is rendered
 * with all digits masked. Use the alternate form ({@code %#s}) to show delimiters:
 * <pre>{@code
 * SSN ssn = new SSN("123-45-6789");
 * String.format("%s", ssn);      // Returns "#########"
 * String.format("%#s", ssn);     // Returns "###-##-####"
 * String.format("%.4s", ssn);    // Returns "#####6789"
 * String.format("%#.4s", ssn);   // Returns "###-##-6789"
 * }</pre>
 *
 * <p>This class is final to prevent subclasses from undermining the security protections.
 *
 * @see EIN
 * @see UsTIN
 */
public final class SSN extends UsTIN {
    public static final Segment AREA = new Segment("area", 3);
    public static final Segment GROUP = new Segment("group", 2);
    public static final Segment SERIAL = new Segment("serial", 4);

    private static final Segment[] EXPECTED = { AREA, GROUP, SERIAL };

    private static final Pattern SSN_PATTERN = Pattern.compile(
            AREA.regex() + "-" + GROUP.regex() + "-" + SERIAL.regex()
    );

    /**
     * Creates an SSN from individual string segments.
     *
     * @param area   the 3-digit area number
     * @param group  the 2-digit group number
     * @param serial the 4-digit serial number
     * @throws InvalidTINException if any segment is invalid
     */
    public SSN(final String area, final String group, final String serial) {
        super(validateSegments(EXPECTED, area, group, serial));
    }

    /**
     * Creates an SSN from individual integer segments.
     *
     * @param area   the area number (1-999)
     * @param group  the group number (1-99)
     * @param serial the serial number (1-9999)
     * @throws InvalidTINException if any segment is out of range
     */
    public SSN(final int area, final int group, final int serial) {
        super(validateIntSegments(EXPECTED, area, group, serial));
    }

    /**
     * Parses an SSN from a formatted string.
     *
     * <p>Accepts formats: {@code ###-##-####} or {@code #########}
     *
     * @param value the SSN string to parse
     * @throws InvalidTINException if the format is invalid or value is null
     */
    public SSN(final CharSequence value) {
        super(parse(SSN_PATTERN, value));
    }

    /**
     * Returns the 3-digit area number.
     *
     * @return the area number, or empty string if unavailable
     */
    public String getArea() {
        final CharSequence area = getValue(0);
        return area == null ? "" : area.toString();
    }

    /**
     * Returns the 2-digit group number.
     *
     * @return the group number, or empty string if unavailable
     */
    public String getGroup() {
        final CharSequence group = getValue(1);
        return group == null ? "" : group.toString();
    }

    /**
     * Returns the 4-digit serial number.
     *
     * @return the serial number, or empty string if unavailable
     */
    public String getSerial() {
        final CharSequence serial = getValue(2);
        return serial == null ? "" : serial.toString();
    }
}
