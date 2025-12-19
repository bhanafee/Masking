package com.maybeitssquid.sensitive;

/**
 * Renders sensitive data as a {@link CharSequence} with appropriate redaction.
 * Implementations control how much information is revealed based on formatting parameters.
 *
 * <h2>Precision Semantics</h2>
 * The precision parameter controls how much unredacted data to show:
 * <ul>
 *     <li>{@code precision = -1}: Default behavior (typically shows half the data, implementation-dependent)</li>
 *     <li>{@code precision >= 0}: Number of unredacted segments/characters to show
 *         (e.g., for SSN "123-45-6789", precision=4 shows last 4 digits: "***-**-6789")</li>
 * </ul>
 * Note: Higher precision values show MORE data, not less. Precision=0 typically shows no data.
 *
 * <h2>Alternate Flag</h2>
 * The alternate flag provides an alternative rendering mode:
 * <ul>
 *     <li>{@code alternate = false}: Standard redacted rendering</li>
 *     <li>{@code alternate = true}: Alternative rendering (implementation-defined, often fully unredacted)</li>
 * </ul>
 * The alternate flag is triggered by the '#' flag in format strings (e.g., {@code "%#s"}).
 *
 * <h2>Usage</h2>
 * Renderers are typically used by subclasses of {@link Sensitive} that override
 * {@link Sensitive#getRenderer()} to provide a shared renderer instance:
 * <pre>{@code
 * public class SecretCode extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER = (value, precision, alternate) -> {
 *         if (alternate) return value;
 *         return "***";
 *     };
 *
 *     public SecretCode(String value) {
 *         super(value);
 *     }
 *
 *     @Override
 *     protected Renderer<String> getRenderer() {
 *         return RENDERER;
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of data to render
 * @see Sensitive
 * @see Sensitive#getRenderer()
 * @see java.util.Formattable
 * @see java.util.FormattableFlags#ALTERNATE
 */
@FunctionalInterface
public interface Renderer<T> {
    /**
     * Renders the contained data with appropriate redaction.
     *
     * @param t         the data to render
     * @param precision the number of unredacted segments to show (or -1 for default)
     * @param alternate whether to use alternate rendering mode
     * @return the rendered (possibly redacted) representation
     */
    CharSequence apply(T t, int precision, boolean alternate);
}
