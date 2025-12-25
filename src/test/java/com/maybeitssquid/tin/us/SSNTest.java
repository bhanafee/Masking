package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

class SSNTest {

    @Nested
    class Formattable {
        @Test
        void defaultFormatMasksValue() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("#####6789", String.format("%s", ssn));
        }

        @Test
        void precisionControlsVisibleDigits() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("####56789", String.format("%.5s", ssn));
            assertEquals("###456789", String.format("%.6s", ssn));
            assertEquals("123456789", String.format("%.9s", ssn));
        }

        @Test
        void zeroPrecisionMasksAll() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("#########", String.format("%.0s", ssn));
        }

        @Test
        void highPrecisionShowsAll() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("123456789", String.format("%.100s", ssn));
        }

        @Test
        void alternateFormPreservesDelimiters() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("###-##-6789", String.format("%#s", ssn));
        }

        @Test
        void alternateFormWithPrecision() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("###-#5-6789", String.format("%#.5s", ssn));
            assertEquals("###-45-6789", String.format("%#.6s", ssn));
            assertEquals("123-45-6789", String.format("%#.9s", ssn));
        }

        @Test
        void alternateFormZeroPrecision() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("###-##-####", String.format("%#.0s", ssn));
        }

        @Test
        void widthPadsOutput() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("    #####6789", String.format("%13s", ssn));
        }

        @Test
        void leftJustifyWithWidth() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("#####6789    ", String.format("%-13s", ssn));
        }

        @Test
        void uppercaseFlag() {
            SSN ssn = new SSN("123-45-6789");
            // Digits and # don't change with uppercase, but flag should be accepted
            assertEquals("#####6789", String.format("%S", ssn));
        }

        @Test
        void combinedFlags() {
            SSN ssn = new SSN("123-45-6789");
            assertEquals("###-##-6789  ", String.format("%#-13s", ssn));
        }
    }


    @Test
    void parseInvalidFormat() {
        assertThrows(InvalidTINException.class, () -> new SSN("1234-5-6789"));
        assertThrows(InvalidTINException.class, () -> new SSN("123-45-678"));
        assertThrows(InvalidTINException.class, () -> new SSN(""));
        assertThrows(InvalidTINException.class, () -> new SSN("abc-de-fghi"));
    }

    @Test
    void constructFromStrings() {
        SSN ssn = new SSN("123", "45", "6789");
        assertEquals("123-45-6789", String.format("%#.9s", ssn));
    }

    @Test
    void constructFromStringsNull() {
        assertThrows(NullPointerException.class, () -> new SSN(null, "45", "6789"));
        assertThrows(NullPointerException.class, () -> new SSN("123", null, "6789"));
        assertThrows(NullPointerException.class, () -> new SSN("123", "45", null));
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
        assertEquals(new SSN("123-45-6789"), ssn);
    }

    @Test
    void constructFromIntsWithLeadingZeros() {
        SSN ssn = new SSN(1, 2, 3);
        assertEquals("001-02-0003", String.format("%#.9s", ssn));
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
        assertEquals(new SSN("123", "45", "6789"), ssn);
        SSN ssn2 = new SSN("123456789");
        assertEquals(new SSN("123", "45", "6789"), ssn);
    }

    @Test
    void constructFromCharSequenceNull() {
        assertThrows(NullPointerException.class, () -> new SSN((CharSequence) null));
    }

    @Test
    void constructFromCharSequenceInvalid() {
        assertThrows(InvalidTINException.class, () -> new SSN("invalid"));
    }

    @Test
    void toStringMasksValue() {
        SSN ssn = new SSN("123-45-6789");
        assertEquals("#####6789", ssn.toString());
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