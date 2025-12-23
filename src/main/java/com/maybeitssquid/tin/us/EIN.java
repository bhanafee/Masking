package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;

import java.util.regex.Pattern;

/**
 * An Employer Identification Number (EIN), also known as a Federal Tax Identification Number.
 *
 * <p>EINs are nine-digit numbers in the format {@code CC-SSSSSSS} where:
 * <ul>
 *     <li>CC is the campus code (2 digits) indicating the IRS campus that issued the number</li>
 *     <li>SSSSSSS is the serial number (7 digits)</li>
 * </ul>
 *
 * <p>This class provides automatic masking when formatted. By default, the EIN is rendered
 * with all digits masked. Use the alternate form ({@code %#s}) to show delimiters:
 * <pre>{@code
 * EIN ein = new EIN("12-3456789");
 * String.format("%s", ein);      // Returns "#########"
 * String.format("%#s", ein);     // Returns "##-#######"
 * String.format("%.4s", ein);    // Returns "#####6789"
 * String.format("%#.4s", ein);   // Returns "##-###6789"
 * }</pre>
 *
 * <p>This class is final to prevent subclasses from undermining the security protections.
 *
 * @see SSN
 * @see UsTIN
 */
public final class EIN extends UsTIN {
    public static final Segment CAMPUS = new Segment("campus", 2);
    public static final Segment SERIAL = new Segment("serial", 7);
    private static final Segment[] EXPECTED = { CAMPUS, SERIAL };

    private static final Pattern EIN_PATTERN = Pattern.compile(
            CAMPUS.regex() + "-" + SERIAL.regex()
    );

    /**
     * Creates an EIN from individual string segments.
     *
     * @param campus the 2-digit campus code
     * @param serial the 7-digit serial number
     * @throws InvalidTINException if any segment is invalid
     */
    public EIN(String campus, String serial) {
        super(validateSegments(EXPECTED, campus, serial));
    }

    /**
     * Creates an EIN from individual integer segments.
     *
     * @param campus the campus code (1-99)
     * @param serial the serial number (1-9999999)
     * @throws InvalidTINException if any segment is out of range
     */
    public EIN(int campus, int serial) {
        super(validateIntSegments(EXPECTED, campus, serial));
    }

    /**
     * Parses an EIN from a formatted string.
     *
     * <p>Accepts formats: {@code ##-#######} or {@code #########}
     *
     * @param value the EIN string to parse
     * @throws InvalidTINException if the format is invalid or value is null
     */
    public EIN(final CharSequence value) {
        super(parse(EIN_PATTERN, value));
    }

}
