package com.maybeitssquid.sensitive;

import java.util.function.IntPredicate;

/**
 * Factory methods for creating {@link Renderer} instances.
 *
 * <p>These factory methods are designed to be used in subclasses of {@link Sensitive} that
 * override {@link Sensitive#getRenderer()} to provide custom rendering behavior. The returned
 * renderer instances can be stored as static constants for efficient sharing across instances.
 *
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * // Simple masking subclass - mask leading characters, show last 4
 * public class AccountNumber extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER = Renderers.masked();
 *
 *     public AccountNumber(String value) { super(value); }
 *
 *     @Override
 *     protected Renderer<String> getRenderer() { return RENDERER; }
 * }
 *
 * AccountNumber acct = new AccountNumber("1234567890");
 * System.out.printf("%.4s", acct); // prints "######7890"
 *
 * // Selective masking - preserve delimiters, mask only digits
 * public class SSN extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER =
 *         Renderers.masked(Character::isDigit);
 *
 *     public SSN(String value) { super(value); }
 *
 *     @Override
 *     protected Renderer<String> getRenderer() { return RENDERER; }
 * }
 *
 * SSN ssn = new SSN("123-45-6789");
 * System.out.printf("%.4s", ssn); // prints "###-##-6789"
 * }</pre>
 *
 * @see Sensitive#getRenderer()
 * @see Sensitive#getAltRenderer()
 */
public class Renderers {

    private Renderers() {
        // EMPTY
    };

    /**
     * Default replacement character for masking.
     */
    public static final char DEFAULT_MASK = '#';

    /**
     * Returns a renderer that shows the value completely unredacted.
     *
     * @param <T> the type of CharSequence to render
     * @return a renderer that returns the input unchanged
     */
    public static <T extends CharSequence> Renderer<T> unredacted() {
        return (cs, p) -> cs == null ? "" : cs;
    }

    /**
     * Returns a renderer that truncates the beginning of the value, showing only
     * the trailing characters based on precision.
     *
     * <p>If precision is negative, shows the last half of the characters (rounded down).
     * Otherwise, shows at most {@code precision} trailing characters.
     *
     * @param <T> the type of CharSequence to render
     * @return a renderer that truncates leading characters
     */
    public static <T extends CharSequence> Renderer<T> truncated() {
        return (cs, p) -> cs == null ? "" : cs.subSequence(Renderers.redactions(p, cs.length()), cs.length());
    }

    /**
     * Returns a renderer that masks leading characters with the specified mask character.
     *
     * <p>If precision is negative, masks the first half of the characters (rounded up).
     * Otherwise, masks all but the last {@code precision} characters.
     *
     * @param <T>  the type of CharSequence to render
     * @param maskCodePoint the code point of the character to use for masking
     * @return a renderer that masks leading characters
     */
    public static <T extends CharSequence> Renderer<T> masked(final int maskCodePoint) {
        final String mask = Character.toString(maskCodePoint);
        return (cs, p) -> {
            if (cs == null) return "";
            final int redactions = Renderers.redactions(p, cs.length());
            return mask.repeat(redactions) + cs.subSequence(redactions, cs.length());
        };
    }

    /**
     * Convenience function equivalent to {@code masked((int) mask)}.
     *
     * @param <T>  the type of CharSequence to render
     * @param mask the character to use for masking. The character must be on the Basic Multilingual Plane.
     * @return a renderer that masks leading characters
     * @throws IllegalArgumentException if the mask is not on the Basic Multilingual Plane.
     * @see #masked(int) 
     */
    public static <T extends CharSequence> Renderer<T> masked(final char mask) {
        if (Character.isSurrogate(mask))
            throw new IllegalArgumentException("Use code point to specify a mask value outside the Basic Multilingual Plane");
        return masked((int) mask);
    }

    /**
     * Convenience function equivalent to {@code masked(DEFAULT_MASK)}.
     *
     * @param <T> the type of CharSequence to render
     * @return a renderer that masks leading characters with '#'
     * @see #masked(int)
     */
    public static <T extends CharSequence> Renderer<T> masked() {
        return masked((int) DEFAULT_MASK);
    }

    /**
     * Returns a renderer that masks only characters matching the predicate, using the
     * specified mask character.
     *
     * <p>Characters not matching the predicate (such as delimiters) are preserved in place.
     * The precision applies only to matching characters. For example, masking an SSN
     * "123-45-6789" with a digit predicate and precision 4 would produce "###-##-6789".
     *
     * @param <T>         the type of CharSequence to render
     * @param redactable  predicate that returns true for characters that are candidates for redaction
     * @param maskCodePoint the code point of the character to use for masking
     * @return a renderer that selectively masks characters
     */
    public static <T extends CharSequence> Renderer<T> masked(IntPredicate redactable, final int maskCodePoint) {
        final IntPredicate predicate = redactable == null ? c -> true : redactable;
        return (cs, p) -> {
            if (cs == null) return "";
            final long significant = cs.codePoints().filter(predicate).count();
            final int redactions = Renderers.redactions(p, (int) significant);
            final StringBuilder builder = new StringBuilder(cs.length());

            int redacted = 0;
            var iterator = cs.codePoints().iterator();
            while (iterator.hasNext()) {
                int codePoint = iterator.nextInt();
                if (redacted < redactions && predicate.test(codePoint)) {
                    builder.appendCodePoint(maskCodePoint);
                    redacted += 1;
                } else {
                    builder.appendCodePoint(codePoint);
                }
            }
            return builder.toString();
        };
    }

    /**
     * Convenience function equivalent to {@code masked(redactable, (int) mask)}.
     *
     * @param <T>         the type of CharSequence to render
     * @param redactable  predicate that returns true for characters that are candidates for redaction
     * @param mask        the character to use for masking
     * @return a renderer that selectively masks characters
     */
    public static <T extends CharSequence> Renderer<T> masked(IntPredicate redactable, final char mask) {
        if (Character.isSurrogate(mask))
            throw new IllegalArgumentException("Use code point to specify a mask value outside the Basic Multilingual Plane");
        return masked(redactable, (int) mask);
    }

    /**
     * Convenience function equivalent to {@code masked(redactable, DEFAULT_MASK}.
     *
     * @param <T>      the type of CharSequence to render
     * @param maskable predicate that returns true for characters that should be masked
     * @return a renderer that selectively masks characters
     * @see #masked(IntPredicate, int)
     */
    public static <T extends CharSequence> Renderer<T> masked(IntPredicate maskable) {
        return masked(maskable, (int) DEFAULT_MASK);
    }

    /**
     * Returns a renderer that joins an array of CharSequences with a delimiter and applies
     * the given renderer.
     *
     * @param <T>       the element type of the CharSequence array
     * @param renderer  the renderer to apply to the joined string
     * @param delimiterCodePoint the character to insert between array elements
     * @return a renderer for arrays of CharSequences
     */
    public static <T extends CharSequence> Renderer<T[]> join(final Renderer<CharSequence> renderer, final int delimiterCodePoint) {
        if (renderer == null) throw new NullPointerException("Nested renderer is required");
        final String delimiter = Character.toString(delimiterCodePoint);
        return (cs, p) -> cs == null ? "" : renderer.apply(String.join(delimiter, cs), p);
    }

    /**
     * Returns a renderer that joins an array of CharSequences with a delimiter and applies
     * the given renderer.
     *
     * @param <T>       the element type of the CharSequence array
     * @param renderer  the renderer to apply to the joined string
     * @param delimiter the character to insert between array elements
     * @return a renderer for arrays of CharSequences
     * @see #join(Renderer, int)
     */
    public static <T extends CharSequence> Renderer<T[]> join(final Renderer<CharSequence> renderer, final char delimiter) {
        if (Character.isSurrogate(delimiter))
            throw new IllegalArgumentException("Use code point to specify a delimiter value outside the Basic Multilingual Plane");
        return join(renderer, (int) delimiter);
    }

    /**
     * Returns a renderer that joins an array of CharSequences and applies the given renderer.
     *
     * <p>The array elements are concatenated without a delimiter before rendering.
     *
     * @param <T>      the element type of the CharSequence array
     * @param renderer the renderer to apply to the joined string
     * @return a renderer for arrays of CharSequences
     */
    public static <T extends CharSequence> Renderer<T[]> join(final Renderer<CharSequence> renderer) {
        if (renderer == null) throw new NullPointerException("Nested renderer is required");
        return (cs, p) -> cs == null ? "" : renderer.apply(String.join("", cs), p);
    }

    /**
     * Computes the number of symbols to redact. If precision &lt; 0, returns half the length (rounded up),
     * otherwise returns length - precision.
     *
     * @param precision the number of unredacted symbols requested, or -1 if default is desired.
     * @param length    the number of symbols in the unredacted field.
     * @return the number of unredacted symbols to show.
     */
    public static int redactions(final int precision, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be >= 0, got " + length);
        } else if (precision < 0) {
            return (length + 1) / 2;
        } else {
            return Math.max(0, length - precision);
        }
    }

}
