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
 * @param <T> The type of sensitive data to be protected.
 */
@SuppressWarnings("unused")
public class Sensitive<T> implements Formattable {

    protected final T contained;

    private final Renderer<T> renderer;

    public Sensitive(final Renderer<T> renderer, final T contained) {
        if (contained == null) throw new NullPointerException("Sensitive value cannot be null");
        this.contained = contained;
        // TODO: replace ignored with _ when JEP 443 "unnamed variable" is available
        this.renderer = renderer == null ? (ignored, precision, alternate) -> "" : renderer;
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
        final CharSequence redacted = renderer.apply(contained, precision, alternate);

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
        return contained.hashCode();
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

        return contained.equals(sensitive.contained);
    }

    @FunctionalInterface
    public interface Renderer<T> {
        CharSequence apply(T t, int precision, boolean alternate);
    }

}
