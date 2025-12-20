package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SSNTest {

    @Test
    void parseValid() {
        String[] parsed = SSN.parse("123-45-6789");
        assertArrayEquals(new String[]{"123", "45", "6789"}, parsed);
    }

    @Test
    void parseNull() {
        assertThrows(InvalidTINException.class, () -> SSN.parse(null));
    }

    @Test
    void parseInvalidFormat() {
        assertThrows(InvalidTINException.class, () -> SSN.parse("123456789"));
        assertThrows(InvalidTINException.class, () -> SSN.parse("12-345-6789"));
        assertThrows(InvalidTINException.class, () -> SSN.parse("1234-5-6789"));
        assertThrows(InvalidTINException.class, () -> SSN.parse("123-45-678"));
        assertThrows(InvalidTINException.class, () -> SSN.parse(""));
        assertThrows(InvalidTINException.class, () -> SSN.parse("abc-de-fghi"));
    }

    @Test
    void constructFromStrings() {
        SSN ssn = new SSN("123", "45", "6789");
        assertEquals("123", ssn.getArea());
        assertEquals("45", ssn.getGroup());
        assertEquals("6789", ssn.getSerial());
    }

    @Test
    void constructFromStringsNull() {
        assertThrows(InvalidTINException.class, () -> new SSN(null, "45", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", null, "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "45", null));
    }

    @Test
    void constructFromStringsInvalidLength() {
        assertThrows(InvalidTINException.class, () -> new SSN("12", "45", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("1234", "45", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "4", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "456", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "45", "678"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "45", "67890"));
    }

    @Test
    void constructFromStringsNonDigits() {
        assertThrows(InvalidTINException.class, () -> new SSN("abc", "45", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "ab", "6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123", "45", "abcd"));
    }

    @Test
    void constructFromInts() {
        SSN ssn = new SSN(123, 45, 6789);
        assertEquals("123", ssn.getArea());
        assertEquals("45", ssn.getGroup());
        assertEquals("6789", ssn.getSerial());
    }

    @Test
    void constructFromIntsWithLeadingZeros() {
        SSN ssn = new SSN(1, 2, 3);
        assertEquals("001", ssn.getArea());
        assertEquals("02", ssn.getGroup());
        assertEquals("0003", ssn.getSerial());
    }

    @Test
    void constructFromIntsOutOfRange() {
        assertThrows(InvalidTINException.class, () -> new SSN(-1, 45, 6789));
        assertThrows(InvalidTINException.class, () -> new SSN(1000, 45, 6789));
        assertThrows(InvalidTINException.class, () -> new SSN(123, -1, 6789));
        assertThrows(InvalidTINException.class, () -> new SSN(123, 100, 6789));
        assertThrows(InvalidTINException.class, () -> new SSN(123, 45, -1));
        assertThrows(InvalidTINException.class, () -> new SSN(123, 45, 10000));
    }

    @Test
    void constructFromCharSequence() {
        SSN ssn = new SSN("123-45-6789");
        assertEquals("123", ssn.getArea());
        assertEquals("45", ssn.getGroup());
        assertEquals("6789", ssn.getSerial());
    }

    @Test
    void constructFromCharSequenceNull() {
        assertThrows(InvalidTINException.class, () -> new SSN((CharSequence) null));
    }

    @Test
    void constructFromCharSequenceInvalid() {
        assertThrows(InvalidTINException.class, () -> new SSN("invalid"));
    }

    @Test
    void toStringDoesNotExposeValue() {
        SSN ssn = new SSN("123-45-6789");
        String str = ssn.toString();
        assertFalse(str.contains("123"));
        assertFalse(str.contains("6789"));
    }

    @Test
    void equalsSameValue() {
        SSN ssn1 = new SSN("123-45-6789");
        SSN ssn2 = new SSN("123", "45", "6789");
        SSN ssn3 = new SSN(123, 45, 6789);
        assertEquals(ssn1, ssn2);
        assertEquals(ssn2, ssn3);
        assertEquals(ssn1, ssn3);
    }

    @Test
    void equalsDifferentValue() {
        SSN ssn1 = new SSN("123-45-6789");
        SSN ssn2 = new SSN("123-45-6780");
        assertNotEquals(ssn1, ssn2);
    }

    @Test
    void hashCodeConsistent() {
        SSN ssn1 = new SSN("123-45-6789");
        SSN ssn2 = new SSN(123, 45, 6789);
        assertEquals(ssn1.hashCode(), ssn2.hashCode());
    }
}