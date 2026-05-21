package com.maybeitssquid.sensitive;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SensitiveTest {

  private final Object containedObj = new Object();

  private final String containedString = "test case";

  private final Sensitive<Object> sensitiveObj = new Sensitive<>(containedObj);

  private final Sensitive<String> sensitiveString = new Sensitive<>(containedString);

  private final Sensitive<String> sensitiveRendered =
      new Sensitive<>(containedString) {
        @Override
        protected Renderer<String> getRenderer() {
          return (v, p) -> v;
        }
      };

  @Test
  void formatTo() {
    // Default always renders an empty string
    assertEquals("", "%s".formatted(sensitiveString));
    assertEquals("", "%s".formatted(sensitiveObj));

    // Check width
    assertEquals(" ", "%1s".formatted(sensitiveObj));
    assertEquals("  ", "%2s".formatted(sensitiveObj));
    assertEquals("   ", "%3s".formatted(sensitiveObj));

    // Can't check justification or upper case using default rendition, use a pass-through
    assertEquals(containedString, "%s".formatted(sensitiveRendered));
  }

  @Test
  void testToString() {
    assertEquals("", sensitiveString.toString());
    assertEquals("", sensitiveObj.toString());

    assertEquals("test case", "%s".formatted(sensitiveRendered));
    assertEquals("test case", "%1s".formatted(sensitiveRendered));
    assertEquals(" test case", "%10s".formatted(sensitiveRendered));
    assertEquals("   test case", "%12s".formatted(sensitiveRendered));
    assertEquals("test case ", "%-10s".formatted(sensitiveRendered));
    assertEquals("test case   ", "%-12s".formatted(sensitiveRendered));
    assertEquals("TEST CASE", "%S".formatted(sensitiveRendered));
    assertEquals("test case", "%#s".formatted(sensitiveRendered));
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

      assertThrows(
          NotSerializableException.class,
          () -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(wrapper);
          });
    }
  }
}
