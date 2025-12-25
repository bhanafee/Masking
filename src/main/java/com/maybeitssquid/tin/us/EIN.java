package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;

import java.util.regex.Matcher;
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
    private static final String CAMPUS = digits("campus", 2);
    private static final String SERIAL = digits("serial", 7);

    private static final Pattern CAMPUS_PATTERN = Pattern.compile(String.format("^%s$", CAMPUS));
    private static final Pattern SERIAL_PATTERN = Pattern.compile(String.format("^%s$", SERIAL));
    private static final Pattern EIN_PATTERN = Pattern.compile(String.format("^%1$s%3$s?%2$s$", CAMPUS, SERIAL, DELIMITER));

    /**
     * Creates an EIN from individual string segments.
     *
     * @param campus the 2-digit campus code
     * @param serial the 7-digit serial number
     * @throws InvalidTINException if any segment is invalid
     */
    public EIN(String campus, String serial) {
        super(validate(campus, serial));
    }

    /**
     * Creates an EIN from individual integer segments.
     *
     * @param campus the campus code (1-99)
     * @param serial the serial number (1-9999999)
     * @throws InvalidTINException if any segment is out of range
     */
    public EIN(int campus, int serial) {
        this(String.format("%02d", campus), String.format("%07d", serial));
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
        super(parse(value));
    }

    private static CharSequence[] validate(final CharSequence campus, final CharSequence serial) {
        if (!CAMPUS_PATTERN.matcher(campus).matches()) throw new InvalidTINException("Invalid EIN, group part must be 2 digits");
        if (!SERIAL_PATTERN.matcher(serial).matches()) throw new InvalidTINException("Invalid EIN, serial part must be 7 digits");
        return new CharSequence[]{campus, serial};
    }

    private static CharSequence[] parse(final CharSequence raw) {
        final Matcher matcher = EIN_PATTERN.matcher(raw);
        if (!matcher.matches()) throw new InvalidTINException("Invalid EIN");
        return new CharSequence[] {
                matcher.group("campus"),
                matcher.group("serial")
        };
    }

}
