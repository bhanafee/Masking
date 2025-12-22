package com.maybeitssquid.sensitive;

import java.nio.CharBuffer;
import java.util.function.BiFunction;
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
 * // Simple masking subclass - show last 4 digits
 * public class SSN extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER = Renderers.simple(
 *         Renderers.Extractor.string(),
 *         RegexRedactors.mask('-')
 *     );
 *
 *     public SSN(String value) { super(value); }
 *
 *     @Override
 *     protected Renderer<String> getRenderer() { return RENDERER; }
 * }
 *
 * SSN ssn = new SSN("123-45-6789");
 * System.out.printf("%.4s", ssn); // prints "###-##-6789"
 *
 * // Alternate rendering for admin context
 * public class CreditCard extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER = Renderers.alternateIsUnredacted(
 *         Renderers.Extractor.string(),
 *         RegexRedactors.DEFAULT_MASK
 *     );
 *
 *     public CreditCard(String value) { super(value); }
 *
 *     @Override
 *     protected Renderer<String> getRenderer() { return RENDERER; }
 * }
 *
 * CreditCard card = new CreditCard("4111-1111-1111-1111");
 * System.out.printf("%s", card);   // prints "####-####-####-1111"
 * System.out.printf("%#s", card);  // prints "4111-1111-1111-1111" (unredacted)
 * }</pre>
 *
 * @see Sensitive#getRenderer()
 * @see Sensitive#getAltRenderer()
 */
@SuppressWarnings("unused")
public class Renderers {
    /**
     * Default delimiter between fields.
     */
    public static final char DEFAULT_DELIMITER = '-';

    /**
     * Default replacement character for masking.
     */
    public static final char DEFAULT_MASK = '#';

    public static <T extends CharSequence> Renderer<T> unredacted() {
        return (cs, p) -> cs;
    }

    public static <T extends CharSequence> Renderer<T> truncated() {
        return (cs, p) -> cs.subSequence(Renderers.redact(p, cs.length()), cs.length());
    }

    public static <T extends CharSequence> Renderer<T> masked() {
        return masked(DEFAULT_MASK);
    }

    public static <T extends CharSequence> Renderer<T> masked(final char mask) {
        return (cs, p) -> {
            final int redact = Renderers.redact(p, cs.length());
            return Character.toString(mask).repeat(redact) +
                    cs.subSequence(redact, cs.length());
        };
    }

    public static<T extends CharSequence> Renderer<T> masked(IntPredicate maskable) {
        return masked(DEFAULT_MASK);
    }

    public static<T extends CharSequence> Renderer<T> masked(IntPredicate maskable, final char mask) {
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
     * Computes the number of characters to redact. If precision < 0, returns half the length (rounded up),
     * otherwise returns length - precision.
     *
     * @param precision the number of unredacted characters requested, or -1 if default is desired.
     * @param length the number of characters in the unredacted field.
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

    public interface Redactor extends BiFunction<Integer, CharSequence, CharSequence> {

        /**
         * Redactor that returns the entire unredacted input {@link CharSequence}.
         */
        Redactor PASS_THROUGH = (p, cs) -> cs;
    }

}
