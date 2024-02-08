package com.maybeitssquid.tin.us;

import com.maybeitssquid.sensitive.Sensitive;

abstract public class TIN extends Sensitive<String[]> {

    public TIN(final String... raw) {
        super(raw);
    }

    public static TIN create(final CharSequence raw) {
        return create(raw, false);
    }

    public static TIN create(final CharSequence raw, final boolean preferEIN) {
        if (raw == null) throw new NullPointerException("Cannot create a TIN from a null");
        else return switch (raw.length()) {
            case 0 -> throw new IllegalArgumentException("Cannot parse empty TIN");
            case 9 -> preferEIN ? new EIN(raw) : new SSN(raw); // Expecting #########
            case 10 -> new EIN(raw);                           // Expecting ##-#######
            case 11 -> new SSN(raw);                           // Expecting ###-##-####
            default -> throw new IllegalArgumentException("Cannot identify TIN to parse");
        };
    }
}
