package com.maybeitssquid.tin.us;

import com.maybeitssquid.sensitive.Renderer;
import com.maybeitssquid.sensitive.Renderers;
import com.maybeitssquid.sensitive.Segmented;
import com.maybeitssquid.tin.NationalTIN;

import java.util.Locale;

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
     * Create a named regex group that matches an exact number of digits.
     *
     * @param name the name of the regex group
     * @param length the number of digits to match
     * @return a named regex group that matches exactly {@code length} digits
     */
    public static String digits(final String name, final int length) {
        return String.format("(?<%s>\\d{%d})", name, length);
    }

    /** The delimiter character used between TIN segments. */
    public static final char DELIMITER = '-';

    private static final Renderer<CharSequence[]> MASKED = Renderers.concatenate(Renderers.mask());
    private static final Renderer<CharSequence[]> MASKED_DELIMITED =
            Renderers.delimit(Renderers.mask(c -> c != DELIMITER), DELIMITER);

    /**
     * Creates a new US TIN with the specified segments.
     *
     * @param segments the validated segment strings
     */
    protected UsTIN(final CharSequence... segments) {
        super(segments);
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
