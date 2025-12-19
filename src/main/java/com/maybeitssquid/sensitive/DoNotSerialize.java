package com.maybeitssquid.sensitive;

import java.util.function.Supplier;

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
public class DoNotSerialize<T> implements Supplier<T> {

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
