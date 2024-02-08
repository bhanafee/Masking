package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import com.maybeitssquid.sensitive.Renderers.Extractor;

import static org.junit.jupiter.api.Assertions.*;

class CharSequenceExtractorsTest {
    private final String[] empty = new String[0];
    private final String[] single = new String[] {"test"};
    private final String[] multiple = new String[] {"concatenation", "test", "case"};

    private final Extractor<String> identity = CharSequenceExtractors.identity();
    private final Extractor<String[]> concatenate = CharSequenceExtractors.concatenate();
    private final Extractor<String[]> delimitDefault = CharSequenceExtractors.delimit();
    private final Extractor<String[]> delimitSlash = CharSequenceExtractors.delimit('/');

    @Test
    void identity() {
        assertEquals("", identity.apply(""));
        assertEquals("test", identity.apply("test"));
    }

    @Test
    void concatenate() {
        assertEquals("", concatenate.apply(empty));
        assertEquals("test", concatenate.apply(single));
        assertEquals("concatenationtestcase", concatenate.apply(multiple));
    }

    @Test
    void delimit() {
        assertEquals("", delimitDefault.apply(empty));
        assertEquals("test", delimitDefault.apply(single));
        assertEquals("concatenation-test-case", delimitDefault.apply(multiple));
    }

    @Test
    void testDelimit() {
        assertEquals("", delimitSlash.apply(empty));
        assertEquals("test", delimitSlash.apply(single));
        assertEquals("concatenation/test/case", delimitSlash.apply(multiple));
    }
}