package com.maybeitssquid.sensitive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RendererTest {

    @Test
    void emptyRendererReturnsEmptyStringRegardlessOfInput() {
        Renderer<String> empty = Renderer.empty();
        assertEquals("", empty.apply("secret", -1));
        assertEquals("", empty.apply("secret", 5));
    }

    @Test
    void customRendererReceivesProvidedValueAndPrecision() {
        Renderer<Integer> renderer = (value, precision) -> value + ":" + precision;
        assertEquals("42:3", renderer.apply(42, 3));
    }
}