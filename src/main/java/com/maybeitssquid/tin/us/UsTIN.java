package com.maybeitssquid.tin.us;

import com.maybeitssquid.sensitive.Renderer;
import com.maybeitssquid.sensitive.Renderers;
import com.maybeitssquid.sensitive.Segmented;
import com.maybeitssquid.tin.InvalidTINException;
import com.maybeitssquid.tin.NationalTIN;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract base class for United States Taxpayer Identification Numbers.
 *
 * <p>This class provides common functionality for US TINs including:
 * <ul>
 *     <li>Segment-based validation and parsing</li>
 *     <li>Masked rendering with delimiter preservation</li>
 *     <li>Factory method for parsing ambiguous TIN formats</li>
 * </ul>
 *
 * <p>Subclasses define the specific segment structure for their TIN type.
 * Currently supported types are {@link SSN} and {@link EIN}.
 *
 * <h2>Rendering</h2>
 * <p>By default, US TINs are rendered with all digits masked (no delimiters).
 * The alternate form ({@code %#s}) renders with delimiters preserved:
 * <pre>{@code
 * UsTIN tin = new SSN("123-45-6789");
 * String.format("%s", tin);   // "#########"
 * String.format("%#s", tin);  // "###-##-####"
 * }</pre>
 *
 * @see SSN
 * @see EIN
 * @see NationalTIN
 */
public abstract class UsTIN extends Segmented<CharSequence> implements NationalTIN {

    /**
     * Defines a segment of a TIN with a name and fixed length.
     *
     * <p>Segments are used to validate and parse individual parts of a TIN.
     * Each segment has a name (for error messages and regex group naming)
     * and a length (1-9 digits).
     *
     * @param name   the segment name, used in error messages and regex groups
     * @param length the number of digits in this segment (1-9)
     */
    public record Segment (String name, int length) {
        public Segment {
            Objects.requireNonNull(name, "Segment name must not be null");
            if (name.isBlank()) throw new IllegalArgumentException("Segment name must not be blank");
            if (length < 1 || length > 9) throw new IllegalArgumentException("Segment length must be in the range 1-9");
        }
        private int min() {
            return 1;
        }
        private int max() {
            return switch (length) {
                case 1 -> 9;
                case 2 -> 99;
                case 3 -> 999;
                case 4 -> 9999;
                case 5 -> 99999;
                case 6 -> 999999;
                case 7 -> 9999999;
                case 8 -> 99999999;
                case 9 -> 999999999;
                default -> 0;
            };
        }
        public String validate(final int value) {
            if (value < min() || value > max()) throw new InvalidTINException(
                    String.format("Invalid %s: expected range %d-%d", name, min(), max())
            );
            return String.format("%0" + length + "d", value);
        }
        public String validate(final CharSequence value) {
            if (value == null) throw new InvalidTINException(String.format("Segment %s cannot be null", name));
            if (!value.toString().matches(regex())) throw new InvalidTINException(String.format("Invalid %s segment: expected %d digits (length: %d)", name,length, value.length()));
            return value.toString();
        }
        public String regex() {
            return String.format("(?<%s>\\d{%d})", name, length);
        }
    }

    /**
     * Validates an array of string segments against expected segment definitions.
     *
     * @param expected the expected segment definitions
     * @param segments the string values to validate
     * @return an array of validated segment strings
     * @throws InvalidTINException if segments are null, wrong count, or invalid format
     */
    public static String[] validateSegments(final Segment[] expected, final CharSequence... segments) {
        Objects.requireNonNull(expected, "Expected segments must not be null");
        if (segments == null || segments.length != expected.length) throw new InvalidTINException("Missing or too many TIN segments");
        final String[] validated = new String[expected.length];
        for (int i = 0; i < expected.length; i++) {
            validated[i] = expected[i].validate(segments[i]);
        }
        return validated;
    }

    /**
     * Validates an array of integer segments against expected segment definitions.
     *
     * @param expected the expected segment definitions
     * @param segments the integer values to validate
     * @return an array of validated segment strings (zero-padded to correct length)
     * @throws InvalidTINException if segments are null, wrong count, or out of range
     */
    public static String[] validateIntSegments(final Segment[] expected, final int... segments) {
        Objects.requireNonNull(expected, "Expected segments must not be null");
        if (segments == null || segments.length != expected.length) throw new InvalidTINException("Missing or too many TIN segments");
        final String[] validated = new String[expected.length];
        for (int i = 0; i < expected.length; i++) {
            validated[i] = expected[i].validate(segments[i]);
        }
        return validated;
    }

    /**
     * Parses a TIN string using the given regex pattern.
     *
     * @param expected the pattern to match, with capturing groups for each segment
     * @param value    the string to parse
     * @return an array of segment strings extracted from the input
     * @throws InvalidTINException if value is null or doesn't match the pattern
     */
    public static String[] parse(final Pattern expected, final CharSequence value) {
        if (value == null) throw new InvalidTINException("Value cannot be null");
        final Matcher matcher = expected.matcher(value);
        if (!matcher.matches()) throw new InvalidTINException("Invalid TIN format");
        final String[] segments = new String[matcher.groupCount()];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = matcher.group(i + 1);
        }
        return segments;
    }

    /**
     * Creates a US TIN from a string, inferring the type from the format.
     *
     * <p>Equivalent to {@code create(raw, false)}.
     *
     * @param raw the TIN string to parse
     * @return an {@link SSN} or {@link EIN} depending on the format
     * @throws InvalidTINException if the format cannot be identified
     * @see #create(CharSequence, boolean)
     */
    public static UsTIN create(final CharSequence raw) {
        return create(raw, false);
    }

    /**
     * Creates a US TIN from a string, inferring the type from the format.
     *
     * <p>The type is determined by length:
     * <ul>
     *     <li>9 digits without delimiter: ambiguous, use {@code preferEIN} to choose</li>
     *     <li>10 characters ({@code ##-#######}): EIN</li>
     *     <li>11 characters ({@code ###-##-####}): SSN</li>
     * </ul>
     *
     * @param raw       the TIN string to parse
     * @param preferEIN if true, parse 9-digit inputs as EIN; otherwise as SSN
     * @return an {@link SSN} or {@link EIN} depending on the format
     * @throws InvalidTINException if the format cannot be identified or is invalid
     */
    public static UsTIN create(final CharSequence raw, final boolean preferEIN) {
        if (raw == null) throw new InvalidTINException("Cannot create a TIN from null");
        else return switch (raw.length()) {
            case 0 -> throw new InvalidTINException("Cannot parse empty TIN");
            case 9 -> preferEIN ? new EIN(raw) : new SSN(raw); // Expecting #########
            case 10 -> new EIN(raw);                           // Expecting ##-#######
            case 11 -> new SSN(raw);                           // Expecting ###-##-####
            default -> throw new InvalidTINException("Cannot identify TIN format (length: " + raw.length() + ")");
        };
    }

    /** The delimiter character used between TIN segments. */
    public static final char DELIMITER = '-';

    private static final Renderer<CharSequence[]> MASKED = Renderers.join(Renderers.masked());
    private static final Renderer<CharSequence[]> MASKED_DELIMITED =
            Renderers.join(Renderers.masked(Character::isDigit), DELIMITER);

    /**
     * Creates a new US TIN with the specified segments.
     *
     * @param raw the validated segment strings
     */
    public UsTIN(final String... raw) {
        super(raw);
    }

    @Override
    protected Renderer<CharSequence[]> getRenderer() {
        return MASKED;
    }

    @Override
    protected Renderer<CharSequence[]> getAltRenderer() {
        return MASKED_DELIMITED;
    }

    @Override
    public Locale issuer() {
        return Locale.US;
    }
}
