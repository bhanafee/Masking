package com.maybeitssquid.sensitive;

import com.maybeitssquid.sensitive.Renderers.Extractor;

/**
 * Extractors for use with contained {@link CharSequence} data.
 */
@SuppressWarnings("unused")
public class CharSequenceExtractors {

    /**
     * Returns an extractor for contained items that implement {@link CharSequence} by invoking
     * {@link CharSequence#toString()}.
     *
     * @return the String representation of the object
     * @param <T> any type that extends {@link CharSequence}
     */
    public static <T extends CharSequence> Extractor<T> identity() {
        return raw -> raw == null ? "" : raw.toString();
    }

    /**
     * Returns an extractor for contained items that are arrays of elements that implement {@link CharSequence}.
     * The extraction concatenates the contained items in order.
     *
     * @return a concatenating extractor
     * @param <T> any element type that extends {@link CharSequence}
     */
    public static <T extends CharSequence> Extractor<T[]> concatenate() {
        return raw -> raw == null ? "" : String.join("", raw);
    }

    /**
     * Returns an extractor for contained items that are arrays of elements that implement {@link CharSequence}.
     * The extraction concatenates the contained items in order and separated by {@link Renderers#DEFAULT_DELIMITER}.
     *
     * @return an extractor that delimits elements
     * @param <T> any element type that extends {@link CharSequence}
     */
    public static <T extends CharSequence> Extractor<T[]> delimit() {
        return delimit(Renderers.DEFAULT_DELIMITER);
    }

    /**
     * Returns an extractor for contained items that are arrays of elements that implement {@link CharSequence}.
     * The extraction concatenates the contained items in order and separated by a provided delimiter..
     *
     * @param delimiter the delimiter to insert between elements
     * @return an extractor that delimits elements
     * @param <T> any element type that extends {@link CharSequence}
     */
    public static <T extends CharSequence> Extractor<T[]> delimit(final char delimiter) {
        final String d = Character.toString(delimiter);
        return raw -> raw == null ? "" : String.join(d, raw);
    }
}
