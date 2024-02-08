package com.maybeitssquid.tin.us;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container for an SSN or ITIN.
 */
public class SSN extends TIN {

    public static final String SSN_REGEX = "(?<area>\\d{3})?-(?<group>\\d{2})?-(?<serial>\\d{4})";

    private static final Pattern SSN_PATTERN = Pattern.compile(SSN_REGEX);

    public static String[] parse(final CharSequence value) {
        final Matcher matcher = SSN_PATTERN.matcher(value);
        if (matcher.matches()) {
            return new String[]{
                    matcher.group("area"),
                    matcher.group("group"),
                    matcher.group("serial")
            };
        } else {
            return new String[0];
        }
    }

    public SSN(final String area, final String group, final String serial) {
        super(area, group, serial);
    }

    public SSN(final int area, final int group, final int serial) {
        this(
                String.format(Locale.US, "%03d", area),
                String.format(Locale.US, "%02d", group),
                String.format(Locale.US, "%04d", serial)
        );
    }

    public SSN(final CharSequence value) {
        super(parse(value));
    }

    public String getArea() {
        return contained[0];
    }

    public String getGroup() {
        return contained[1];
    }

    public String getSerial() {
        return contained[2];
    }
}
