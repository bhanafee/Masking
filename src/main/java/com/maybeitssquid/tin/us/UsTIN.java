package com.maybeitssquid.tin.us;

import com.maybeitssquid.sensitive.Renderer;
import com.maybeitssquid.sensitive.Renderers;
import com.maybeitssquid.sensitive.Segmented;
import com.maybeitssquid.tin.InvalidTINException;
import com.maybeitssquid.tin.NationalTIN;

import java.util.Locale;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class UsTIN extends Segmented<CharSequence> implements NationalTIN {
    public record Segment (String name, int length) {
        public Segment {
            Objects.requireNonNull(name, "Segment name must not be null");
            if (name.isBlank()) throw new IllegalArgumentException("Segment name must not be blank");
            if (length < 1 || length > 9) throw new IllegalArgumentException("Segment length must be in the range 1-9");
        }
        private int min() {
            return 1;
        }
        private int max() {
            return switch (length) {
                case 1 -> 9;
                case 2 -> 99;
                case 3 -> 999;
                case 4 -> 9999;
                case 5 -> 99999;
                case 6 -> 999999;
                case 7 -> 9999999;
                case 8 -> 99999999;
                case 9 -> 999999999;
                default -> 0;
            };
        }
        public String validate(final int value) {
            if (value < min() || value > max()) throw new InvalidTINException(
                    String.format("Invalid %s: expected range %d-%d", name, min(), max())
            );
            return String.format("%0" + length + "d", value);
        }
        public String validate(final CharSequence value) {
            if (value == null) throw new InvalidTINException(String.format("Segment %s cannot be null", name));
            if (!value.toString().matches(regex())) throw new InvalidTINException(String.format("Invalid %s segment: expected %d digits (length: %d)", name,length, value.length()));
            return value.toString();
        }
        public String regex() {
            return String.format("(?<%s>\\d{%d})", name, length);
        }
    }

    public static String[] validateSegments(final Segment[] expected, final CharSequence... segments) {
        assert expected != null;
        if (segments == null || segments.length != expected.length) throw new InvalidTINException("Missing or too many TIN segments");
        final String[] validated = new String[expected.length];
        for (int i = 0; i < expected.length; i++) {
            validated[i] = expected[i].validate(segments[i]);
        }
        return validated;
    }

    public static String[] validateIntSegments(final Segment[] expected, final int... segments) {
        assert expected != null;
        if (segments == null || segments.length != expected.length) throw new InvalidTINException("Missing or too many TIN segments");
        final String[] validated = new String[expected.length];
        for (int i = 0; i < expected.length; i++) {
            validated[i] = expected[i].validate(segments[i]);
        }
        return validated;
    }

    public static String[] parse(final Pattern expected, final CharSequence value) {
        if (value == null) throw new InvalidTINException("Value cannot be null");
        final Matcher matcher = expected.matcher(value);
        if (!matcher.matches()) throw new InvalidTINException("Invalid TIN format");
        final String[] segments = new String[matcher.groupCount()];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = matcher.group(i + 1);
        }
        return segments;
    }

    public static UsTIN create(final CharSequence raw) {
        return create(raw, false);
    }

    public static UsTIN create(final CharSequence raw, final boolean preferEIN) {
        if (raw == null) throw new InvalidTINException("Cannot create a TIN from null");
        else return switch (raw.length()) {
            case 0 -> throw new InvalidTINException("Cannot parse empty TIN");
            case 9 -> preferEIN ? new EIN(raw) : new SSN(raw); // Expecting #########
            case 10 -> new EIN(raw);                           // Expecting ##-#######
            case 11 -> new SSN(raw);                           // Expecting ###-##-####
            default -> throw new InvalidTINException("Cannot identify TIN format (length: " + raw.length() + ")");
        };
    }

    public static final char DELIMITER = '-';

    private static final Renderer<CharSequence[]> masked = Renderers.join(Renderers.masked());
    private static final Renderer<CharSequence[]> maskedDelimited =
            Renderers.join(Renderers.masked(c -> c != DELIMITER), DELIMITER);

    public UsTIN(final String... raw) {
        super(raw);
    }

    @Override
    protected Renderer<CharSequence[]> getRenderer() {
        return masked;
    }

    @Override
    protected Renderer<CharSequence[]> getAltRenderer() {
        return maskedDelimited;
    }

    @Override
    public Locale issuer() {
        return Locale.US;
    }
}
