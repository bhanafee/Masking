package com.maybeitssquid.sensitive;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Container for sensitive data to protect it being inadvertently rendered as a plain {@link String}.
 * Subclasses must ensure that {@link Formattable#formatTo(Formatter, int, int, int)} by default does
 * not disclose too much information, such as by truncating or masking the value.
 * The strategy for ensuring that formatted renditions of this class do not disclose sensitive data
 * is defined by a {@link Renderer}. The renderer represents the contained object as a {@link CharSequence}
 * with sensitive data redacted. The default renderer produces an empty sequence.
 *
 * <h2>Rendering Model</h2>
 * The rendering strategy is provided by the {@link #getRenderer()} method, which subclasses override
 * to define how their sensitive data should be formatted. This design allows many instances of the same
 * subclass to share a single renderer instance, typically defined as a static constant.
 *
 * <p>The default implementation of {@code getRenderer()} returns a renderer that produces an empty
 * string, ensuring no sensitive data is disclosed by default.
 *
 * <h3>Example Subclass</h3>
 * <pre>{@code
 * public class MaskedSecret extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER = Renderers.masked();
 *
 *     public MaskedSecret(String value) {
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
 * <h2>Storage Model</h2>
 * Sensitive data is stored internally via a {@link Supplier Supplier&lt;T&gt;} rather than directly.
 * This indirection enables flexible storage strategies, particularly for controlling serialization behavior.
 * When using the convenience constructor that accepts a value directly, the value is automatically
 * wrapped in a {@link DoNotSerialize} supplier, which prevents the value from surviving Java serialization.
 *
 * <p>For custom storage behavior, use the constructor that accepts a {@code Supplier<T>} directly:
 * <pre>{@code
 * // Default: value will not survive serialization
 * Sensitive<String> safe = new Sensitive<>("secret");
 *
 * // Custom supplier for alternative storage strategies
 * Sensitive<String> custom = new Sensitive<>(() -> retrieveFromSecureStore());
 *
 * // Lambda returning a constant: value WILL survive serialization
 * // Use this when you need the object to be serializable
 * Sensitive<String> serializable = new Sensitive<>(() -> "secret");
 * }</pre>
 *
 * <h2>Serialization Protection</h2>
 * By default, this class provides automatic protection against inadvertent serialization of sensitive data.
 * When constructed with a raw value, the value is wrapped in {@link DoNotSerialize}, which:
 * <ul>
 *     <li>Does not implement {@link java.io.Serializable}</li>
 *     <li>Causes any attempt to serialize the containing object to fail with
 *         {@link java.io.NotSerializableException}</li>
 * </ul>
 *
 * <p>This fail-fast behavior ensures that sensitive data cannot be accidentally exposed through
 * Java serialization, logging frameworks that serialize objects, or distributed caches.
 *
 * <p><b>Important:</b> If you need to serialize objects containing sensitive data (e.g., for session
 * storage in Redis, Memcached, or distributed session stores), you must explicitly handle this by:
 * <ul>
 *     <li>Storing only non-sensitive identifiers and retrieving sensitive data on-demand</li>
 *     <li>Providing a custom {@code Supplier<T>} that implements appropriate serialization behavior</li>
 *     <li>Implementing custom {@code writeObject()}/{@code readObject()} methods in subclasses</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * Instances of this class are immutable and thread-safe, provided the contained data type {@code T}
 * is itself immutable or properly synchronized, and the {@code Supplier<T>} is thread-safe.
 *
 * <h2>Subclassing Guidelines</h2>
 * Subclasses extending this class for custom behavior MUST:
 * <ul>
 *     <li>Override {@link #getRenderer()} to provide custom rendering (typically returning a shared static instance)</li>
 *     <li>Not override {@code toString()} to expose sensitive data</li>
 *     <li>Override {@code getContained()} only to add additional protection (e.g., cloning)</li>
 *     <li>Document any security implications of their custom behavior</li>
 * </ul>
 *
 * @param <T> The type of sensitive data to be protected.
 * @see DoNotSerialize
 * @see Renderer
 * @see #getRenderer()
 */
public class Sensitive<T> implements Formattable {

    /**
     * The supplier that provides the sensitive value. Using a supplier allows flexible
     * storage strategies, including protection against serialization via {@link DoNotSerialize}.
     */
    protected final Supplier<T> supplier;

    /**
     * Creates a new Sensitive container with the specified supplier.
     *
     * @param supplier the supplier providing the sensitive value; must not be {@code null}. The supplier is expected
     *                 to return the same value every time.
     * @throws NullPointerException if contained is {@code null}
     */
    public Sensitive(final Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "Sensitive value supplier cannot be null");
        this.supplier = supplier;
    }

    /**
     * Convenience constructor that wraps the value in a {@link DoNotSerialize} supplier.
     *
     * @param value the sensitive value to wrap
     */
    public Sensitive(final T value) {
        this(new DoNotSerialize<>(value));
    }

    /**
     * Returns the renderer used to format this sensitive value. The renderer is used
     * by {@link #formatTo(Formatter, int, int, int)} to transform the value into a
<<<<<<< HEAD
     * string exposes the sensitive data only to the specified precision.
=======
     * partially redacted string that exposes the sensitive data only to the specified
     * precision.
>>>>>>> 865ab59 (Clarified examples for getRenderer() and getAltRenderer() javadoc. Expanded README.md.)
     *
     * <p>The default implementation returns a renderer that produces an empty string,
     * ensuring no sensitive data is disclosed by default. Subclasses should override
     * this method to provide custom rendering behavior, typically returning a shared
     * static renderer instance.
     *
     * <p>A Renderer is expected to be stateless and thread-safe. There should be no reason
<<<<<<< HEAD
     * to create more than one renderer instance for a given subclass. The
     * implementation normally returns a private static singleton. Using a private but
     * non-static renderer causes a new instance to be created for every {@code Sensitive}
     * value, and constructing the renderer dynamically inside {@code getRenderer()} (for
     * example, by calling a static factory like those in {@code Renderers} on every call)
     * creates even more temporary objects.
=======
     * to create more than one renderer instance for a given subclass. The implementation
     * normally returns a private static singleton. Using a private but non-static renderer
     * causes a new instance to be created for every {@code Sensitive} value. Constructing
     * the renderer dynamically inside this method, such as by calling a static factory in
     * {@link Renderers} on every call, creates even more temporary objects.
>>>>>>> 865ab59 (Clarified examples for getRenderer() and getAltRenderer() javadoc. Expanded README.md.)
     *
     * <pre>{@code
     * // Good: shared static singleton
     * private static final Renderer<String> RENDERER = Renderers.masked();
     *
     * @Override
     * protected Renderer<String> getRenderer() {
     *     return RENDERER;
     * }
     *
     * // Bad: non-static instance field causes per-instance renderer creation
     * private final Renderer<String> renderer = Renderers.masked();
     *
     * @Override
     * protected Renderer<String> getRenderer() {
<<<<<<< HEAD
     *     return renderer;
=======
     *     return this.renderer;
>>>>>>> 865ab59 (Clarified examples for getRenderer() and getAltRenderer() javadoc. Expanded README.md.)
     * }
     *
     * // Bad: dynamic creation on each call
     * @Override
     * protected Renderer<String> getRenderer() {
     *     return Renderers.masked();
     * }
     * }</pre>
     *
     * @return the renderer for this sensitive value; never {@code null}
     */
    protected Renderer<T> getRenderer() {
        return Renderer.empty();
    }

    /**
     * Returns the renderer used to format this sensitive value when the alternate form is specified.
     *
     * <p>The default implementation delegates to {@link #getRenderer()}. Override this method to
     * provide an alternate rendition when using {@code String.format("%#s", this)}, such as
     * showing the value in a commonly used human-readable form.
     *
     * <h3>Example</h3>
     * <pre>{@code
     * public class MySecret extends Sensitive<String> {
     *     private static final Renderer<String> TRUNCATED = Renderers.truncated();
     *     private static final Renderer<String> MASKED = Renderers.masked('?');
     *
     *     @Override
     *     protected Renderer<String> getRenderer() { return MASKED; }
     *
     *     @Override
     *     protected Renderer<String> getAltRenderer() { return UNMASKED; }
     * }
     *
     * MySecret secret = new MySecret("secret123");
     * String.format("%s", secret);   // Returns "t123"      (truncated)
     * String.format("%#s", secret);  // Returns "?????t123" (masked with question marks)
     * }</pre>
     *
     * @return the alternate renderer for this sensitive value; never {@code null}
     */
    protected Renderer<T> getAltRenderer() {
        return getRenderer();
    }

    /**
     * Returns the sensitive value. Subclasses may access this to implement custom behavior. Subclasses may override to
     * provide additional protection (e.g., cloning arrays). Use with caution to avoid exposing sensitive data.
     *
     * <p><b>Security Note:</b> This method provides direct access to sensitive data for subclass
     * implementation purposes. Avoid calling this method from public APIs or in contexts where
     * the result might be logged, serialized, or otherwise persisted.
     *
     * @return the contained sensitive value, or {@code null} if deserialized
     */
    protected T getValue() {
        return supplier.get();
    }

    /**
     * Generates a format string to apply the parts of the formatting instructions that are not covered by the renderer.
     *
     * @param width the minimum width of the output
     * @param left  whether the output should be left-justified
     * @param upper whether the output should be converted to uppercase
     * @return a format string
     */
    protected String residualFormat(final int width, final boolean left, final boolean upper) {
        return String.join("",
                "%",
                left ? "-" : "",
                width == -1 ? "" : String.valueOf(width),
                upper ? "S" : "s");
    }

    /**
     * Formats this sensitive value according to the specified flags, width, and precision.
     *
     * <p>The rendering behavior is determined by the {@link Renderer} returned by
     * {@link #getRenderer()} or {@link #getAltRenderer()} (when the alternate flag is set).
     * Width, left-justification, and uppercase flags are applied after rendering.
     *
     * @param formatter the formatter to write to
     * @param flags     formatting flags (see {@link FormattableFlags})
     * @param width     the minimum number of characters; -1 for no minimum
     * @param precision passed to the renderer to control redaction; -1 for default
     */
    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        final boolean alternate = (flags & FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
        final boolean upper = ((flags & FormattableFlags.UPPERCASE) == FormattableFlags.UPPERCASE);
        final boolean left = ((flags & FormattableFlags.LEFT_JUSTIFY) == FormattableFlags.LEFT_JUSTIFY);

        final Renderer<T> renderer = alternate ? getAltRenderer() : getRenderer();
        Objects.requireNonNull(renderer, "Unexpected null renderer");
        final CharSequence redacted = renderer.apply(this.supplier.get(), precision);

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
        return Objects.hashCode(this.supplier.get());
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

        Sensitive<?> other = (Sensitive<?>) o;

        return Objects.equals(this.supplier.get(), other.supplier.get());
    }

    /**
     * A {@link Supplier} implementation that wraps a value in a {@code transient} field,
     * preventing the value from being serialized via standard Java serialization.
     *
     * <p>This class is designed to be used with {@link Sensitive} to provide automatic
     * protection against inadvertent serialization of sensitive data. When an instance
     * of this class is serialized, the contained value will be lost (deserialized as {@code null}).
     *
     * <h2>Usage</h2>
     * <pre>{@code
     * DoNotSerialize<String> wrapped = new DoNotSerialize<>("secret");
     * String value = wrapped.get(); // Returns "secret"
     *
     * // After serialization and deserialization:
     * String value = deserializedWrapped.get(); // Returns null
     * }</pre>
     *
     * <h2>Thread Safety</h2>
     * Instances of this class are immutable and thread-safe, provided the contained
     * value type {@code T} is itself immutable or properly synchronized.
     *
     * @param <T> the type of value to protect from serialization
     * @see Sensitive
     */
    public static class DoNotSerialize<T> implements Supplier<T> {

        private final transient T value;

        /**
         * Creates a new instance wrapping the specified value.
         *
         * @param value the value to wrap; will not be serialized
         */
        public DoNotSerialize(final T value) {
            this.value = value;
        }

        /**
         * Returns the contained value, or {@code null} if this instance was deserialized.
         *
         * @return the contained value, or {@code null} after deserialization
         */
        @Override
        public T get() {
            return value;
        }
    }
}
