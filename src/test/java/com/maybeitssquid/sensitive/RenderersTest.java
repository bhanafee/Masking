package com.maybeitssquid.sensitive;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.IntPredicate;

class RenderersTest {

    @Nested
    class Constants {
        @Test
        void defaultMaskIsHash() {
            assertEquals('#', Renderers.DEFAULT_MASK);
        }
    }

    @Nested
    class Unredacted {
        @Test
        void returnsEntireInput() {
            Renderer<String> renderer = Renderers.unredacted();
            assertEquals("secret", renderer.apply("secret", -1));
        }

        @Test
        void ignoresPrecision() {
            Renderer<String> renderer = Renderers.unredacted();
            assertEquals("secret", renderer.apply("secret", 0));
            assertEquals("secret", renderer.apply("secret", 3));
            assertEquals("secret", renderer.apply("secret", 100));
        }

        @Test
        void handlesEmptyInput() {
            Renderer<String> renderer = Renderers.unredacted();
            assertEquals("", renderer.apply("", -1));
        }
    }

    @Nested
    class Truncated {
        @Test
        void withNegativePrecisionShowsHalf() {
            Renderer<String> renderer = Renderers.truncated();
            // "secret" has 6 chars, redact = (6+1)/2 = 3, shows last 3
            assertEquals("ret", renderer.apply("secret", -1));
        }

        @Test
        void withNegativePrecisionRoundsUpForOddLength() {
            Renderer<String> renderer = Renderers.truncated();
            // "hello" has 5 chars, redact = (5+1)/2 = 3, shows last 2
            assertEquals("lo", renderer.apply("hello", -1));
        }

        @Test
        void withPrecisionShowsLastNCharacters() {
            Renderer<String> renderer = Renderers.truncated();
            assertEquals("cret", renderer.apply("secret", 4));
        }

        @Test
        void withZeroPrecisionReturnsEmpty() {
            Renderer<String> renderer = Renderers.truncated();
            assertEquals("", renderer.apply("secret", 0));
        }

        @Test
        void withPrecisionExceedingLengthReturnsAll() {
            Renderer<String> renderer = Renderers.truncated();
            assertEquals("secret", renderer.apply("secret", 100));
        }

        @Test
        void handlesEmptyInput() {
            Renderer<String> renderer = Renderers.truncated();
            assertEquals("", renderer.apply("", -1));
        }
    }

    @Nested
    class MaskedDefault {
        @Test
        void withNegativePrecisionMasksHalf() {
            Renderer<String> renderer = Renderers.masked();
            // "secret" has 6 chars, redact = 3
            assertEquals("###ret", renderer.apply("secret", -1));
        }

        @Test
        void withPrecisionMasksAllButLastN() {
            Renderer<String> renderer = Renderers.masked();
            assertEquals("##cret", renderer.apply("secret", 4));
        }

        @Test
        void withZeroPrecisionMasksAll() {
            Renderer<String> renderer = Renderers.masked();
            assertEquals("######", renderer.apply("secret", 0));
        }

        @Test
        void withPrecisionExceedingLengthMasksNone() {
            Renderer<String> renderer = Renderers.masked();
            assertEquals("secret", renderer.apply("secret", 100));
        }

        @Test
        void handlesEmptyInput() {
            Renderer<String> renderer = Renderers.masked();
            assertEquals("", renderer.apply("", -1));
        }
    }

    @Nested
    class MaskedWithCustomChar {
        @Test
        void usesCustomMaskCharacter() {
            Renderer<String> renderer = Renderers.masked('*');
            assertEquals("***ret", renderer.apply("secret", -1));
        }

        @Test
        void withZeroPrecisionMasksAllWithCustomChar() {
            Renderer<String> renderer = Renderers.masked('X');
            assertEquals("XXXXXX", renderer.apply("secret", 0));
        }
    }

    @Nested
    class MaskedWithPredicate {
        @Test
        void masksOnlyCharactersMatchingPredicate() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '#');
            // "123-45-6789" has 9 digits, with default precision redact = 5
            // Masks first 5 digits, preserves dashes
            assertEquals("###-##-6789", renderer.apply("123-45-6789", -1));
        }

        @Test
        void preservesNonMatchingCharacters() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '*');
            assertEquals("***-**-6789", renderer.apply("123-45-6789", -1));
        }

        @Test
        void withPrecisionMasksCorrectNumberOfMatchingChars() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '#');
            // 9 digits, precision 4 means redact 5 digits
            assertEquals("###-##-6789", renderer.apply("123-45-6789", 4));
        }

        @Test
        void withZeroPrecisionMasksAllMatchingChars() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '#');
            assertEquals("###-##-####", renderer.apply("123-45-6789", 0));
        }

        @Test
        void withHighPrecisionMasksNone() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '#');
            assertEquals("123-45-6789", renderer.apply("123-45-6789", 100));
        }

        @Test
        void handlesInputWithNoMatchingCharacters() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '#');
            assertEquals("no-digits-here", renderer.apply("no-digits-here", -1));
        }

        @Test
        void handlesEmptyInput() {
            IntPredicate isDigit = Character::isDigit;
            Renderer<String> renderer = Renderers.masked(isDigit, '#');
            assertEquals("", renderer.apply("", -1));
        }
    }

    @Nested
    class RedactMethod {
        @Test
        void withNegativePrecisionReturnsHalfRoundedUp() {
            assertEquals(3, Renderers.redact(-1, 6));
            assertEquals(3, Renderers.redact(-1, 5));
            assertEquals(2, Renderers.redact(-1, 4));
            assertEquals(2, Renderers.redact(-1, 3));
            assertEquals(1, Renderers.redact(-1, 2));
            assertEquals(1, Renderers.redact(-1, 1));
            assertEquals(0, Renderers.redact(-1, 0));
        }

        @Test
        void withZeroPrecisionReturnsFullLength() {
            assertEquals(6, Renderers.redact(0, 6));
        }

        @Test
        void withPositivePrecisionReturnsLengthMinusPrecision() {
            assertEquals(4, Renderers.redact(2, 6));
            assertEquals(2, Renderers.redact(4, 6));
        }

        @Test
        void withPrecisionExceedingLengthReturnsZero() {
            assertEquals(0, Renderers.redact(10, 6));
        }

        @Test
        void withNegativeLengthThrows() {
            assertThrows(IllegalArgumentException.class, () -> Renderers.redact(0, -1));
        }

        @Test
        void negativeLengthExceptionMessageIsDescriptive() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Renderers.redact(0, -5));
            assertTrue(ex.getMessage().contains("-5"));
        }
    }

}