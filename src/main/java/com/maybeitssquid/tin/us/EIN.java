package com.maybeitssquid.tin.us;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for an Employer Identification number.
 */
public class EIN extends TIN {
    public static final String EIN_REGEX = "(?<prefix>\\d{2})?-(?<serial>\\d{7})";

    private static final Pattern EIN_PATTERN = Pattern.compile(EIN_REGEX);

    public static String[] parse(final CharSequence value) {
        final Matcher matcher = EIN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("prefix"),
                    matcher.group("serial")
            };
        } else {
            return new String[0];
        }
    }

    public EIN(String prefix, String serial) {
        super(prefix, serial);
    }

    public EIN(int prefix, int serial) {
        this(
                String.format(Locale.US, "%02d", prefix),
                String.format(Locale.US, "%07d", serial)
        );
    }

    public EIN(final CharSequence value) {
        super(parse(value));
    }

    public String getPrefix() {
        return contained[0];
    }

    public String getSerial() {
        return contained[1];
    }
}
