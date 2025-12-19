package com.maybeitssquid.sensitive;

import java.util.function.BiFunction;
import java.util.function.Function;

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
 */
@SuppressWarnings("unused")
public class Renderers {
    /**
     * Default delimiter between fields.
     */
    public static final char DEFAULT_DELIMITER = '-';

    public static <T> Renderer<T> empty() {
        return (c, p, a) -> "";
    }

    public static <T> Renderer<T> unredacted(final Extractor<T> extractor) {
        return (c, p, a) -> extractor.apply(c);
    }

    public static <T> Renderer<T> unredacted(final Extractor<T> extractor,
                                             final Extractor<T> altExtractor) {
        return (c, p, a) -> (a ? extractor : altExtractor).apply(c);
    }

    public static <T> Renderer<T> simple(final Extractor<T> extractor,
                                         final Redactor redactor) {
        return (c, p, a) -> redactor.apply(p, extractor.apply(c));
    }

    public static <T> Renderer<T> alternateIsUnredacted(final Extractor<T> extractor,
                                                        final Redactor redactor) {
        return (c, p, a) -> a ? extractor.apply(c) : redactor.apply(p, extractor.apply(c));
    }

    public static <T> Renderer<T> alternates(final Extractor<T> extractor,
                                             final Redactor redactor,
                                             final Redactor altRedactor) {
        return (c, p, a) -> (a ? redactor : altRedactor).apply(p, extractor.apply(c));
    }

    public static <T> Renderer<T> alternates(final Extractor<T> extractor,
                                             final Extractor<T> altExtractor,
                                             final Redactor redactor) {
        return (c, p, a) -> redactor.apply(p, (a ? extractor : altExtractor).apply(c));
    }

    public static <T> Renderer<T> alternates(final Extractor<T> extractor,
                                             final Extractor<T> altExtractor,
                                             final Redactor redactor,
                                             final Redactor altRedactor) {
        return (c, p, a) -> a
                ? altRedactor.apply(p, altExtractor.apply(c))
                : redactor.apply(p, extractor.apply(c));
    }

    public interface Extractor<T> extends Function<T, CharSequence> {
        static <T> Extractor<T> empty() {
            // TODO: replace raw with _ when JEP 443 "unnamed variable" is available
            return raw -> "";
        }

        static <T> Extractor<T> string() {
            return Object::toString;
        }
    }

    public interface Redactor extends BiFunction<Integer, CharSequence, CharSequence> {

        /**
         * Redactor that returns the entire unredacted input {@link CharSequence}.
         */
        Redactor PASS_THROUGH = (p, cs) -> cs;
    }

}
