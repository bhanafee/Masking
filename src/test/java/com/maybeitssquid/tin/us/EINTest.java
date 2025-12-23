package com.maybeitssquid.tin.us;

import com.maybeitssquid.tin.InvalidTINException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

class EINTest {

    @Nested
    class Formattable {
        @Test
        void defaultFormatMasksValue() {
            EIN ein = new EIN("12-3456789");
            assertEquals("#####6789", String.format("%s", ein));
        }

        @Test
        void precisionControlsVisibleDigits() {
            EIN ein = new EIN("12-3456789");
            assertEquals("####56789", String.format("%.5s", ein));
            assertEquals("###456789", String.format("%.6s", ein));
            assertEquals("123456789", String.format("%.9s", ein));
        }

        @Test
        void zeroPrecisionMasksAll() {
            EIN ein = new EIN("12-3456789");
            assertEquals("#########", String.format("%.0s", ein));
        }

        @Test
        void highPrecisionShowsAll() {
            EIN ein = new EIN("12-3456789");
            assertEquals("123456789", String.format("%.100s", ein));
        }

        @Test
        void alternateFormPreservesDelimiters() {
            EIN ein = new EIN("12-3456789");
            assertEquals("##-###6789", String.format("%#s", ein));
        }

        @Test
        void alternateFormWithPrecision() {
            EIN ein = new EIN("12-3456789");
            assertEquals("##-##56789", String.format("%#.5s", ein));
            assertEquals("##-#456789", String.format("%#.6s", ein));
            assertEquals("12-3456789", String.format("%#.9s", ein));
        }

        @Test
        void alternateFormZeroPrecision() {
            EIN ein = new EIN("12-3456789");
            assertEquals("##-#######", String.format("%#.0s", ein));
        }

        @Test
        void widthPadsOutput() {
            EIN ein = new EIN("12-3456789");
            assertEquals("    #####6789", String.format("%13s", ein));
        }

        @Test
        void leftJustifyWithWidth() {
            EIN ein = new EIN("12-3456789");
            assertEquals("#####6789    ", String.format("%-13s", ein));
        }

        @Test
        void uppercaseFlag() {
            EIN ein = new EIN("12-3456789");
            // Digits and # don't change with uppercase, but flag should be accepted
            assertEquals("#####6789", String.format("%S", ein));
        }

        @Test
        void combinedFlags() {
            EIN ein = new EIN("12-3456789");
            assertEquals("##-###6789   ", String.format("%#-13s", ein));
        }
    }


    @Test
    void parseInvalidFormat() {
        assertThrows(InvalidTINException.class, () -> new EIN(null));
        assertThrows(InvalidTINException.class, () -> new EIN("123456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("1-23456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("123-456789"));
        assertThrows(InvalidTINException.class, () -> new EIN("12-345678"));
        assertThrows(InvalidTINException.class, () -> new EIN(""));
        assertThrows(InvalidTINException.class, () -> new EIN("ab-cdefghi"));
    }

    @Test
    void constructFromStrings() {
        EIN ein = new EIN("12", "3456789");
        assertEquals("12", ein.getCampus());
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
        assertEquals("12", ein.getCampus());
        assertEquals("3456789", ein.getSerial());
    }

    @Test
    void constructFromIntsWithLeadingZeros() {
        EIN ein = new EIN(1, 23);
        assertEquals("01", ein.getCampus());
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
        assertEquals("12", ein.getCampus());
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
    void toStringMasksValue() {
        EIN ein = new EIN("12-3456789");
        String str = ein.toString();
        assertEquals("12", ein.getCampus());
        assertEquals("3456789", ein.getSerial());
        assertEquals("#####6789", str);
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
