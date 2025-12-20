package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EINTest {

    @Test
    void parseValid() {
        String[] parsed = EIN.parse("12-3456789");
        assertArrayEquals(new String[]{"12", "3456789"}, parsed);
    }

    @Test
    void parseNull() {
        assertThrows(InvalidTINException.class, () -> EIN.parse(null));
    }

    @Test
    void parseInvalidFormat() {
        assertThrows(InvalidTINException.class, () -> EIN.parse("123456789"));
        assertThrows(InvalidTINException.class, () -> EIN.parse("1-23456789"));
        assertThrows(InvalidTINException.class, () -> EIN.parse("123-456789"));
        assertThrows(InvalidTINException.class, () -> EIN.parse("12-345678"));
        assertThrows(InvalidTINException.class, () -> EIN.parse(""));
        assertThrows(InvalidTINException.class, () -> EIN.parse("ab-cdefghi"));
    }

    @Test
    void constructFromStrings() {
        EIN ein = new EIN("12", "3456789");
        assertEquals("12", ein.getPrefix());
        assertEquals("3456789", ein.getSerial());
    }

    @Test
    void constructFromStringsNull() {
        assertThrows(InvalidTINException.class, () -> new EIN(null, "3456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("12", null));
    }

    @Test
    void constructFromStringsInvalidLength() {
        assertThrows(InvalidTINException.class, () -> new EIN("1", "3456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("123", "3456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("12", "345678"));
        assertThrows(InvalidTINException.class, () -> new EIN("12", "34567890"));
    }

    @Test
    void constructFromStringsNonDigits() {
        assertThrows(InvalidTINException.class, () -> new EIN("ab", "3456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("12", "abcdefg"));
    }

    @Test
    void constructFromInts() {
        EIN ein = new EIN(12, 3456789);
        assertEquals("12", ein.getPrefix());
        assertEquals("3456789", ein.getSerial());
    }

    @Test
    void constructFromIntsWithLeadingZeros() {
        EIN ein = new EIN(1, 23);
        assertEquals("01", ein.getPrefix());
        assertEquals("0000023", ein.getSerial());
    }

    @Test
    void constructFromIntsOutOfRange() {
        assertThrows(InvalidTINException.class, () -> new EIN(-1, 3456789));
        assertThrows(InvalidTINException.class, () -> new EIN(100, 3456789));
        assertThrows(InvalidTINException.class, () -> new EIN(12, -1));
        assertThrows(InvalidTINException.class, () -> new EIN(12, 10000000));
    }

    @Test
    void constructFromCharSequence() {
        EIN ein = new EIN("12-3456789");
        assertEquals("12", ein.getPrefix());
        assertEquals("3456789", ein.getSerial());
    }

    @Test
    void constructFromCharSequenceNull() {
        assertThrows(InvalidTINException.class, () -> new EIN((CharSequence) null));
    }

    @Test
    void constructFromCharSequenceInvalid() {
        assertThrows(InvalidTINException.class, () -> new EIN("invalid"));
    }

    @Test
    void toStringDoesNotExposeValue() {
        EIN ein = new EIN("12-3456789");
        String str = ein.toString();
        assertFalse(str.contains("12"));
        assertFalse(str.contains("3456789"));
    }

    @Test
    void equalsSameValue() {
        EIN ein1 = new EIN("12-3456789");
        EIN ein2 = new EIN("12", "3456789");
        EIN ein3 = new EIN(12, 3456789);
        assertEquals(ein1, ein2);
        assertEquals(ein2, ein3);
        assertEquals(ein1, ein3);
    }

    @Test
    void equalsDifferentValue() {
        EIN ein1 = new EIN("12-3456789");
        EIN ein2 = new EIN("12-3456780");
        assertNotEquals(ein1, ein2);
    }

    @Test
    void hashCodeConsistent() {
        EIN ein1 = new EIN("12-3456789");
        EIN ein2 = new EIN(12, 3456789);
        assertEquals(ein1.hashCode(), ein2.hashCode());
    }
}