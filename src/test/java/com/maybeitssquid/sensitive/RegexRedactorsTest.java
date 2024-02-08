package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import static com.maybeitssquid.sensitive.RegexRedactors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexRedactorsTest {

    private final Renderers.Redactor truncateAll = truncate();
    private final Renderers.Redactor truncateAllowables = truncate('-', '/');
    private final Renderers.Redactor truncateARegex = truncate("[0-9]");

    private final Renderers.Redactor maskAll = mask();
    private final Renderers.Redactor maskAllowables = mask('-', '/');

    private final Renderers.Redactor maskDefault = mask('-');

    private final Renderers.Redactor maskWithAllowables = maskWith('*', '-', '/');

    private final Renderers.Redactor maskDefaultRegex = mask("[0-9]");

    private final Renderers.Redactor maskReplacementRegex = maskWith('*',"[0-9]");

    private final Renderers.Redactor redactRegex = redact("_|", "[0-9]");

    @Test
    void truncateAll() {
        assertEquals("test", truncateAll.apply(5, "test"));
        assertEquals("test", truncateAll.apply(4, "test"));
        assertEquals("est", truncateAll.apply(3, "test"));
        assertEquals("st", truncateAll.apply(2, "test"));
        assertEquals("t", truncateAll.apply(1, "test"));
        assertEquals("", truncateAll.apply(0, "test"));
        assertEquals("st", truncateAll.apply(-1, "test"));
    }

    @Test
    void truncateAllowables() {
        assertEquals("test", truncateAllowables.apply(5, "test"));
        assertEquals("te-st", truncateAllowables.apply(4, "te-st"));
        assertEquals("e/st", truncateAllowables.apply(3, "te/st"));
        assertEquals("/t", truncateAllowables.apply(1, "te/st"));
        assertEquals("//s-t", truncateAllowables.apply(-1, "te//s-t"));
    }

    @Test
    void truncateRegex() {
        assertEquals("Te23st", truncateARegex.apply(2, "Te123st"));
        assertEquals("--89", truncateARegex.apply(2, "123-456-789"));
    }

    @Test
    void maskDefaultAllowables() {
        assertEquals("######-6789", maskAll.apply(-1, "123-45-6789"));

        assertEquals("###-##-6789", maskDefault.apply(-1, "123-45-6789"));

        assertEquals("123-45-6", maskDefault.apply(9, "123-45-6"));
        assertEquals("123-45-6", maskDefault.apply(8, "123-45-6"));
        assertEquals("123-45-6", maskDefault.apply(7, "123-45-6"));
        assertEquals("123-45-6", maskDefault.apply(6, "123-45-6"));
        assertEquals("#23-45-6", maskDefault.apply(5, "123-45-6"));
        assertEquals("##3-45-6", maskDefault.apply(4, "123-45-6"));
        assertEquals("###-45-6", maskDefault.apply(3, "123-45-6"));
        assertEquals("###-#5-6", maskDefault.apply(2, "123-45-6"));
        assertEquals("###-##-6", maskDefault.apply(1, "123-45-6"));
        assertEquals("###-##-#", maskDefault.apply(0, "123-45-6"));
        assertEquals("###-45-6", maskDefault.apply(-1, "123-45-6"));

        assertEquals("###-##-6789", maskAllowables.apply(-1, "123-45-6789"));
        assertEquals("###/##-6789", maskAllowables.apply(-1, "123/45-6789"));
    }

    @Test
    void maskReplacementAllowables() {
        assertEquals("123-45/6", maskWithAllowables.apply(9, "123-45/6"));
        assertEquals("123-45/6", maskWithAllowables.apply(8, "123-45/6"));
        assertEquals("123-45/6", maskWithAllowables.apply(7, "123-45/6"));
        assertEquals("123-45/6", maskWithAllowables.apply(6, "123-45/6"));
        assertEquals("*23-45/6", maskWithAllowables.apply(5, "123-45/6"));
        assertEquals("**3-45/6", maskWithAllowables.apply(4, "123-45/6"));
        assertEquals("***-45/6", maskWithAllowables.apply(3, "123-45/6"));
        assertEquals("***-*5/6", maskWithAllowables.apply(2, "123-45/6"));
        assertEquals("***-**/6", maskWithAllowables.apply(1, "123-45/6"));
        assertEquals("***-**/*", maskWithAllowables.apply(0, "123-45/6"));
        assertEquals("***-45/6", maskWithAllowables.apply(-1, "123-45/6"));
    }

    @Test
    void maskDefaultRegex() {
        assertEquals("123-45/6", maskDefaultRegex.apply(9, "123-45/6"));
        assertEquals("123-45/6", maskDefaultRegex.apply(8, "123-45/6"));
        assertEquals("123-45/6", maskDefaultRegex.apply(7, "123-45/6"));
        assertEquals("123-45/6", maskDefaultRegex.apply(6, "123-45/6"));
        assertEquals("#23-45/6", maskDefaultRegex.apply(5, "123-45/6"));
        assertEquals("##3-45/6", maskDefaultRegex.apply(4, "123-45/6"));
        assertEquals("###-45/6", maskDefaultRegex.apply(3, "123-45/6"));
        assertEquals("###-#5/6", maskDefaultRegex.apply(2, "123-45/6"));
        assertEquals("###-##/6", maskDefaultRegex.apply(1, "123-45/6"));
        assertEquals("###-##/#", maskDefaultRegex.apply(0, "123-45/6"));
        assertEquals("###-45/6", maskDefaultRegex.apply(-1, "123-45/6"));
    }

    @Test
    void maskReplacementRegex() {
        assertEquals("123-45/6", maskReplacementRegex.apply(9, "123-45/6"));
        assertEquals("123-45/6", maskReplacementRegex.apply(8, "123-45/6"));
        assertEquals("123-45/6", maskReplacementRegex.apply(7, "123-45/6"));
        assertEquals("123-45/6", maskReplacementRegex.apply(6, "123-45/6"));
        assertEquals("*23-45/6", maskReplacementRegex.apply(5, "123-45/6"));
        assertEquals("**3-45/6", maskReplacementRegex.apply(4, "123-45/6"));
        assertEquals("***-45/6", maskReplacementRegex.apply(3, "123-45/6"));
        assertEquals("***-*5/6", maskReplacementRegex.apply(2, "123-45/6"));
        assertEquals("***-**/6", maskReplacementRegex.apply(1, "123-45/6"));
        assertEquals("***-**/*", maskReplacementRegex.apply(0, "123-45/6"));
        assertEquals("***-45/6", maskReplacementRegex.apply(-1, "123-45/6"));
    }

    @Test
    void redactRegex() {
        assertEquals("_|_|_|-45/6", redactRegex.apply(-1, "123-45/6"));
    }

    @Test
    void redactions() {
        assertEquals(3, RegexRedactors.redactions(3, 6));
        assertEquals(2, RegexRedactors.redactions(3, 5));
        assertEquals(1, RegexRedactors.redactions(3, 4));
        assertEquals(0, RegexRedactors.redactions(3, 3));
        assertEquals(0, RegexRedactors.redactions(3, 2));
        assertEquals(0, RegexRedactors.redactions(3, 1));
        assertEquals(0, RegexRedactors.redactions(3, 0));

        assertEquals(3, RegexRedactors.redactions(-1, 6));
        assertEquals(3, RegexRedactors.redactions(-1, 5));
        assertEquals(2, RegexRedactors.redactions(-1, 4));
        assertEquals(2, RegexRedactors.redactions(-1, 3));
        assertEquals(1, RegexRedactors.redactions(-1, 2));
        assertEquals(1, RegexRedactors.redactions(-1, 1));
        assertEquals(0, RegexRedactors.redactions(-1, 0));

    }
}