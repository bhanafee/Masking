package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;

import java.util.regex.Matcher;
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

    private static final String AREA = digits("area", 3);
    private static final String GROUP = digits("group", 2);
    private static final String SERIAL = digits("serial", 4);

    private static final Pattern AREA_PATTERN = Pattern.compile(String.format("^%s$", AREA));
    private static final Pattern GROUP_PATTERN = Pattern.compile(String.format("^%s$", GROUP));
    private static final Pattern SERIAL_PATTERN = Pattern.compile(String.format("^%s$", SERIAL));
    private static final Pattern SSN_PATTERN = Pattern.compile(String.format("^%1$s%4$s?%2$s%4$s?%3$s$", AREA, GROUP, SERIAL, DELIMITER));

    /**
     * Creates an SSN from individual string segments.
     *
     * @param area   the 3-digit area number
     * @param group  the 2-digit group number
     * @param serial the 4-digit serial number
     * @throws InvalidTINException if any segment is invalid
     */
    public SSN(final CharSequence area, final CharSequence group, final CharSequence serial) {
        super(validate(area, group, serial));
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
        this(String.format("%03d", area), String.format("%02d", group), String.format("%04d", serial));
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
        super(parse(value));
    }

    private static CharSequence[] validate(final CharSequence area, final CharSequence group, final CharSequence serial) {
        if (!AREA_PATTERN.matcher(area).matches()) throw new InvalidTINException("Invalid SSN, area part must be 3 digits");
        if (!GROUP_PATTERN.matcher(group).matches()) throw new InvalidTINException("Invalid SSN, group part must be 2 digits");
        if (!SERIAL_PATTERN.matcher(serial).matches()) throw new InvalidTINException("Invalid SSN, serial part must be 4 digits");
        return new CharSequence[]{area, group, serial};
    }

    private static CharSequence[] parse(final CharSequence raw) {
        final Matcher matcher = SSN_PATTERN.matcher(raw);
        if (!matcher.matches()) throw new InvalidTINException("Invalid SSN");
        return new CharSequence[] {
                matcher.group("area"),
                matcher.group("group"),
                matcher.group("serial")
        };
    }

}
