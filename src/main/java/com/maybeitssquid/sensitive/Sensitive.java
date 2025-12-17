package com.maybeitssquid.sensitive;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Container for sensitive data to protect it being inadvertently rendered as a plain {@link String}.
 * Subclasses must ensure that {@link Formattable#formatTo(Formatter, int, int, int)} by default does
 * not disclose too much information, such as by truncating or masking the value.
 * The strategy for ensuring that formatted renditions of this class do not disclose sensitive data
 * is defined by a {@link Renderer}. The renderer represents the contained object as a {@link CharSequence}
 * with sensitive data redacted. The default renderer produces an empty sequence.
 *
 * <h2>Serialization Warning</h2>
 * This class does not implement {@link java.io.Serializable}. Subclasses that implement
 * {@code Serializable} MUST ensure sensitive data is not exposed through Java serialization.
 * Consider:
 * <ul>
 *     <li>Marking sensitive fields as {@code transient}</li>
 *     <li>Implementing {@code writeObject()} and {@code readObject()} to control serialization</li>
 *     <li>Implementing {@code writeReplace()} to substitute a safe representation</li>
 *     <li>Not implementing {@code Serializable} at all for classes containing truly sensitive data</li>
 * </ul>
 * Java serialization bypasses the rendering protection provided by {@link Formattable}, potentially
 * exposing the raw sensitive data.
 * <p>
 * <b>Important:</b> External session caches (e.g., Redis, Memcached, distributed session stores)
 * typically require Java serialization to persist session data. If sensitive data is marked
 * {@code transient} or serialization is blocked, the data will be lost when the session is
 * externalized. Consider storing only non-sensitive identifiers in sessions and retrieving
 * sensitive data on-demand from secure storage, or implement custom serialization that
 * re-encrypts sensitive data before persistence.
 *
 * <h2>Thread Safety</h2>
 * Instances of this class are immutable and thread-safe, provided the contained data type {@code T}
 * is itself immutable or properly synchronized.
 *
 * @param <T> The type of sensitive data to be protected.
 */
@SuppressWarnings("unused")
public class Sensitive<T> implements Formattable {

    private final T contained;

    private final Renderer<T> renderer;

    public Sensitive(final Renderer<T> renderer, final T contained) {
        if (contained == null) throw new NullPointerException("Sensitive value cannot be null");
        this.contained = contained;
        // TODO: replace ignored with _ when JEP 443 "unnamed variable" is available
        this.renderer = renderer == null ? (ignored, precision, alternate) -> "" : renderer;
    }

    /**
     * Returns the contained sensitive value. Subclasses may access this to implement
     * custom behavior. Subclasses may override to provide additional protection (e.g., cloning arrays).
     * Use with caution to avoid exposing sensitive data.
     *
     * <p><b>Security Note:</b> This method provides direct access to sensitive data for subclass
     * implementation purposes. Avoid calling this method from public APIs or in contexts where
     * the result might be logged, serialized, or otherwise persisted.
     *
     * @return the contained sensitive value
     */
    protected T getContained() {
        return contained;
    }

    public Sensitive(final T contained) {
        this(null, contained);
    }

    /**
     * Generates a format string to apply the parts of the formatting instructions that are not covered by the renderer.
     *
     * @param width the minimum width of the output
     * @param left  whether the output should be left-justified
     * @param upper whether the output should be converted to upper case
     * @return a format string
     */
    protected String residualFormat(final int width, final boolean left, final boolean upper) {
        return String.join("",
                "%",
                left ? "-" : "",
                width == -1 ? "" : String.valueOf(width),
                upper ? "S" : "s");
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        final boolean alternate = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        final boolean upper = ((flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE);
        final boolean left = ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY);

        // TODO: use functional syntax
        final CharSequence redacted = renderer.apply(getContained(), precision, alternate);

        formatter.format(residualFormat(width, left, upper), redacted);
    }

    /**
     * Returns the result of applying default string formatting to this value. Equivalent to
     * {@code String.format("%s", this)}.
     *
     * @return the result of applying default string formatting to this value.
     */
    @Override
    public final String toString() {
        return String.format("%s", this);
    }

    /**
     * Returns the hash of the enclosed {@code raw} data.
     *
     * @return the hash of the enclosed {@code raw} data.
     */
    @Override
    public int hashCode() {
        return getContained().hashCode();
    }

    /**
     * Returns true if the types match and the enclosed raw data are equal.
     *
     * @param o {@inheritDoc}
     * @return if the types match and the enclosed raw data are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sensitive<?> sensitive = (Sensitive<?>) o;

        return getContained().equals(sensitive.getContained());
    }

    /**
     * Renders sensitive data as a {@link CharSequence} with appropriate redaction.
     * Implementations control how much information is revealed based on formatting parameters.
     *
     * <h3>Precision Semantics</h3>
     * The precision parameter controls how much unredacted data to show:
     * <ul>
     *     <li>{@code precision = -1}: Default behavior (typically shows half the data, implementation-dependent)</li>
     *     <li>{@code precision >= 0}: Number of unredacted segments/characters to show
     *         (e.g., for SSN "123-45-6789", precision=4 shows last 4 digits: "***-**-6789")</li>
     * </ul>
     * Note: Higher precision values show MORE data, not less. Precision=0 typically shows no data.
     *
     * <h3>Alternate Flag</h3>
     * The alternate flag provides an alternative rendering mode:
     * <ul>
     *     <li>{@code alternate = false}: Standard redacted rendering</li>
     *     <li>{@code alternate = true}: Alternative rendering (implementation-defined, often fully unredacted)</li>
     * </ul>
     * The alternate flag is triggered by the '#' flag in format strings (e.g., {@code "%#s"}).
     *
     * @see java.util.Formattable
     * @see java.util.FormattableFlags#ALTERNATE
     */
    @FunctionalInterface
    public interface Renderer<T> {
        /**
         * Renders the contained data with appropriate redaction.
         *
         * @param t the data to render
         * @param precision the number of unredacted segments to show (or -1 for default)
         * @param alternate whether to use alternate rendering mode
         * @return the rendered (possibly redacted) representation
         */
        CharSequence apply(T t, int precision, boolean alternate);
    }

}
