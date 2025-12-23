package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveTest {

    private final Object containedObj = new Object();

    private final String containedString = "test case";

    private final Sensitive<Object> sensitiveObj = new Sensitive<>(containedObj);

    private final Sensitive<String> sensitiveString = new Sensitive<>(containedString);

    private final Sensitive<String> sensitiveRendered = new Sensitive<>(containedString) {
        @Override
        protected Renderer<String> getRenderer() {
            return (v, p) -> v;
        }
    };

    @Test
    void formatTo() {
        // Default always renders an empty string
        assertEquals("", String.format("%s", sensitiveString));
        assertEquals("", String.format("%s", sensitiveObj));

        // Check width
        assertEquals(" ", String.format("%1s", sensitiveObj));
        assertEquals("  ", String.format("%2s", sensitiveObj));
        assertEquals("   ", String.format("%3s", sensitiveObj));

        // Can't check justification or upper case using default rendition, use a pass-through
        assertEquals(containedString, String.format("%s", sensitiveRendered));
    }

    @Test
    void testToString() {
        assertEquals("", sensitiveString.toString());
        assertEquals("", sensitiveObj.toString());

        assertEquals("test case", String.format("%s", sensitiveRendered));
        assertEquals("test case", String.format("%1s", sensitiveRendered));
        assertEquals(" test case", String.format("%10s", sensitiveRendered));
        assertEquals("   test case", String.format("%12s", sensitiveRendered));
        assertEquals("test case ", String.format("%-10s", sensitiveRendered));
        assertEquals("test case   ", String.format("%-12s", sensitiveRendered));
        assertEquals("TEST CASE", String.format("%S", sensitiveRendered));
        assertEquals("test case", String.format("%#s", sensitiveRendered));
    }

    @Test
    void testHashCode() {
        assertEquals(containedObj.hashCode(), sensitiveObj.hashCode());
        assertEquals(containedString.hashCode(), sensitiveString.hashCode());
        assertNotEquals(containedObj.hashCode(), sensitiveString.hashCode());
    }

    @SuppressWarnings({"SimplifiableAssertion", "EqualsWithItself"})
    @Test
    void testEquals() {
        assertTrue(sensitiveObj.equals(sensitiveObj));
        assertTrue(sensitiveString.equals(sensitiveString));

        assertTrue(sensitiveString.equals(new Sensitive<>(containedString)));
        assertFalse(sensitiveObj.equals(sensitiveString));
        assertFalse(sensitiveString.equals(sensitiveObj));
    }


    @Nested
    class DoNotSerializeInterface {
        @Test
        void get() {
            String value = "secret";
            Sensitive.DoNotSerialize<String> wrapper = new Sensitive.DoNotSerialize<>(value);
            assertEquals(value, wrapper.get());
            assertSame(value, wrapper.get());
        }

        @Test
        void getWithNull() {
            Sensitive.DoNotSerialize<String> wrapper = new Sensitive.DoNotSerialize<>(null);
            assertNull(wrapper.get());
        }

        @Test
        void serializationFails() {
            String value = "secret";
            Sensitive.DoNotSerialize<String> wrapper = new Sensitive.DoNotSerialize<>(value);

            assertThrows(NotSerializableException.class, () -> {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(wrapper);
            });
        }
    }

}