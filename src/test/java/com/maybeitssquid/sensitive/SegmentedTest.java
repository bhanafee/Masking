package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SegmentedTest {

    private final String[] empty = new String[0];
    private final String[] single = new String[] {"test"};
    private final String[] multiple = new String[] {"segmented", "test", "case"};

    private final Sensitive.Renderer<String[]> renderer = (v, p, a) -> String.join(" ", v);

    private final Segmented<String> emptySegment = new Segmented<>(renderer, empty);

    private final Segmented<String> singleSegment = new Segmented<>(renderer, single);

    private final Segmented<String> multipleSegments = new Segmented<>(renderer, multiple);

    @Test
    void testToString() {
        assertEquals("", emptySegment.toString());
        assertEquals("test", singleSegment.toString());
        assertEquals("segmented test case", multipleSegments.toString());
    }

        @Test
    void testHashCode() {
        assertEquals(Arrays.hashCode(empty), emptySegment.hashCode());
        assertEquals(Arrays.hashCode(single), singleSegment.hashCode());
        assertEquals(Arrays.hashCode(multiple), multipleSegments.hashCode());

        assertNotEquals(Arrays.hashCode(empty), singleSegment.hashCode());
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself"})
    @Test
    void testEquals() {
        assertTrue(emptySegment.equals(emptySegment));
        assertTrue(singleSegment.equals(singleSegment));
        assertTrue(multipleSegments.equals(multipleSegments));
        assertTrue(multipleSegments.equals(new Segmented<>(renderer, multiple)));

        assertFalse(multipleSegments.equals(singleSegment));
        assertFalse(singleSegment.equals(multipleSegments));
        assertFalse(emptySegment.equals(singleSegment));
    }
}