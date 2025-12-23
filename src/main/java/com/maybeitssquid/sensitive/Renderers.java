package com.maybeitssquid.sensitive;

import java.nio.CharBuffer;
import java.util.function.IntPredicate;

/**
 * Factory methods for creating {@link Renderer} instances.
 *
 * <p>These factory methods are designed to be used in subclasses of {@link Sensitive} that
 * override {@link Sensitive#getRenderer()} to provide custom rendering behavior. The returned
 * renderer instances can be stored as static constants for efficient sharing across instances.
 *
 * <h3>Usage Examples</h3>
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
    /**
     * Default replacement character for masking.
     */
    public static final Character DEFAULT_MASK = '#';

    /**
     * Returns a renderer that shows the value completely unredacted.
     *
     * @param <T> the type of CharSequence to render
     * @return a renderer that returns the input unchanged
     */
    public static <T extends CharSequence> Renderer<T> unredacted() {
        return (cs, p) -> cs;
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
        return (cs, p) -> cs.subSequence(Renderers.redact(p, cs.length()), cs.length());
    }

    /**
     * Returns a renderer that masks leading characters with the {@link #DEFAULT_MASK} character.
     *
     * <p>If precision is negative, masks the first half of the characters (rounded up).
     * Otherwise, masks all but the last {@code precision} characters.
     *
     * @param <T> the type of CharSequence to render
     * @return a renderer that masks leading characters with '#'
     * @see #masked(char)
     */
    public static <T extends CharSequence> Renderer<T> masked() {
        return masked(DEFAULT_MASK);
    }

    /**
     * Returns a renderer that masks leading characters with the specified mask character.
     *
     * <p>If precision is negative, masks the first half of the characters (rounded up).
     * Otherwise, masks all but the last {@code precision} characters.
     *
     * @param <T>  the type of CharSequence to render
     * @param mask the character to use for masking
     * @return a renderer that masks leading characters
     */
    public static <T extends CharSequence> Renderer<T> masked(final char mask) {
        final String m = Character.toString(mask);
        return (cs, p) -> {
            final int redact = Renderers.redact(p, cs.length());
            return m.repeat(redact) + cs.subSequence(redact, cs.length());
        };
    }

    /**
     * Returns a renderer that masks only characters matching the predicate, using the
     * {@link #DEFAULT_MASK} character.
     *
     * <p>Characters not matching the predicate (such as delimiters) are preserved in place.
     * The precision applies only to matching characters.
     *
     * @param <T>      the type of CharSequence to render
     * @param maskable predicate that returns true for characters that should be masked
     * @return a renderer that selectively masks characters
     * @see #masked(IntPredicate, char)
     */
    public static <T extends CharSequence> Renderer<T> masked(IntPredicate maskable) {
        return masked(maskable, DEFAULT_MASK);
    }

    /**
     * Returns a renderer that masks only characters matching the predicate, using the
     * specified mask character.
     *
     * <p>Characters not matching the predicate (such as delimiters) are preserved in place.
     * The precision applies only to matching characters. For example, masking an SSN
     * "123-45-6789" with a digit predicate and precision 4 would produce "###-##-6789".
     *
     * @param <T>      the type of CharSequence to render
     * @param maskable predicate that returns true for characters that should be masked
     * @param mask     the character to use for masking
     * @return a renderer that selectively masks characters
     */
    public static <T extends CharSequence> Renderer<T> masked(IntPredicate maskable, final char mask) {
        return (cs, p) -> {
            final int significant = (int) cs.chars().filter(maskable).count();
            final int redact = Renderers.redact(p, significant);
            final CharBuffer buffer = CharBuffer.allocate(cs.length());
            int pos = 0;
            int redacted = 0;
            while (redacted < redact) {
                if (maskable.test(cs.charAt(pos))) {
                    buffer.append(mask);
                    redacted += 1;
                } else {
                    buffer.append(cs.charAt(pos));
                }
                pos += 1;
            }
            buffer.append(cs.subSequence(pos, cs.length()));
            buffer.flip();
            return buffer.toString();
        };
    }

    /**
     * Returns a renderer that joins an array of CharSequences and applies the given renderer.
     *
     * <p>The array elements are concatenated without a delimiter before rendering.
     *
     * @param <T>      the element type of the CharSequence array
     * @param renderer the renderer to apply to the joined string
     * @return a renderer for arrays of CharSequences
     * @see #join(Renderer, char)
     */
    public static <T extends CharSequence> Renderer<T[]> join(final Renderer<CharSequence> renderer) {
        return (cs, p) -> renderer.apply(String.join("", cs), p);
    }

    /**
     * Returns a renderer that joins an array of CharSequences with a delimiter and applies
     * the given renderer.
     *
     * @param <T>       the element type of the CharSequence array
     * @param renderer  the renderer to apply to the joined string
     * @param delimiter the character to insert between array elements
     * @return a renderer for arrays of CharSequences
     */
    public static <T extends CharSequence> Renderer<T[]> join(final Renderer<CharSequence> renderer, final char delimiter) {
        final String d = Character.toString(delimiter);
        return (cs, p) -> renderer.apply(String.join(d, cs), p);
    }

    /**
     * Computes the number of characters to redact. If precision < 0, returns half the length (rounded up),
     * otherwise returns length - precision.
     *
     * @param precision the number of unredacted characters requested, or -1 if default is desired.
     * @param length    the number of characters in the unredacted field.
     * @return the number of unredacted characters to show.
     */
    public static int redact(final int precision, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be >= 0, got " + length);
        } else if (precision < 0) {
            return (length + 1) / 2;
        } else {
            return Math.max(0, length - precision);
        }
    }

}
