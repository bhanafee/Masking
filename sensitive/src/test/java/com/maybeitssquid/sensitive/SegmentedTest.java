package com.maybeitssquid.sensitive;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SegmentedTest {

  /** Test subclass that exposes the protected accessors for direct verification. */
  private static class Exposed<T> extends Segmented<T> {
    Exposed(final T[] value) {
      super(value);
    }

    T[] exposedValue() {
      return getValue();
    }

    T exposedValue(final int index) {
      return getValue(index);
    }
  }

  private final String[] empty = new String[0];
  private final String[] single = new String[] {"test"};
  private final String[] multiple = new String[] {"segmented", "test", "case"};

  private final Renderer<String[]> renderer = (v, p) -> String.join(" ", v);

  private final Segmented<String> emptySegment =
      new Segmented<>(empty) {
        @Override
        protected Renderer<String[]> getRenderer() {
          return renderer;
        }
      };

  private final Segmented<String> singleSegment =
      new Segmented<>(single) {
        @Override
        protected Renderer<String[]> getRenderer() {
          return renderer;
        }
      };

  private final Segmented<String> multipleSegments =
      new Segmented<>(multiple) {
        @Override
        protected Renderer<String[]> getRenderer() {
          return renderer;
        }
      };

  @Nested
  class Construction {
    @Test
    void nullArrayThrows() {
      assertThrows(NullPointerException.class, () -> new Segmented<>((String[]) null));
    }

    @Test
    void defensivelyCopiesConstructorArgument() {
      String[] source = {"a", "b"};
      Exposed<String> segment = new Exposed<>(source);
      source[0] = "mutated";
      // The stored array is a copy, so mutating the source does not affect it.
      assertArrayEquals(new String[] {"a", "b"}, segment.exposedValue());
    }
  }

  @Nested
  class GetValue {
    @Test
    void returnsCopyOfWholeArray() {
      String[] source = {"a", "b", "c"};
      Exposed<String> segment = new Exposed<>(source);
      String[] returned = segment.exposedValue();
      assertArrayEquals(source, returned);
      // Returned array is a defensive copy that can be mutated without affecting the segment.
      returned[0] = "mutated";
      assertArrayEquals(source, segment.exposedValue());
    }

    @Test
    void returnsElementAtIndex() {
      Exposed<String> segment = new Exposed<>(new String[] {"a", "b", "c"});
      assertEquals("a", segment.exposedValue(0));
      assertEquals("b", segment.exposedValue(1));
      assertEquals("c", segment.exposedValue(2));
    }

    @Test
    void indexOutOfRangeReturnsNull() {
      Exposed<String> segment = new Exposed<>(new String[] {"a"});
      assertNull(segment.exposedValue(5));
      assertNull(segment.exposedValue(-1));
    }
  }

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

  @SuppressWarnings({"SimplifiableAssertion", "ConstantValue"})
  @Test
  void testEqualsNullAndOtherType() {
    assertFalse(multipleSegments.equals(null));
    assertFalse(multipleSegments.equals("segmented test case"));
  }
}
