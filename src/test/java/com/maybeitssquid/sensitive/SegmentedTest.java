package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SegmentedTest {

    private final String[] empty = new String[0];
    private final String[] single = new String[] {"test"};
    private final String[] multiple = new String[] {"segmented", "test", "case"};

    private final Renderer<String[]> renderer = (v, p ) -> String.join(" ", v);

    private final Segmented<String> emptySegment = new Segmented<>(empty) {
        @Override
        protected Renderer<String[]> getRenderer() {
            return renderer;
        }
    };

    private final Segmented<String> singleSegment = new Segmented<>(single) {
        @Override
        protected Renderer<String[]> getRenderer() {
            return renderer;
        }
    };

    private final Segmented<String> multipleSegments = new Segmented<>(multiple) {
        @Override
        protected Renderer<String[]> getRenderer() {
            return renderer;
        }
    };

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

        // Test equality with a new instance containing the same data
        Segmented<String> anotherMultiple = new Segmented<>(multiple);
        Segmented<String> yetAnotherMultiple = new Segmented<>(multiple);
        assertTrue(anotherMultiple.equals(yetAnotherMultiple));

        assertFalse(multipleSegments.equals(singleSegment));
        assertFalse(singleSegment.equals(multipleSegments));
        assertFalse(emptySegment.equals(singleSegment));
    }
}