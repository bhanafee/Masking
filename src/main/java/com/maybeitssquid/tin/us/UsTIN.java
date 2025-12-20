package com.maybeitssquid.tin.us;

import com.maybeitssquid.sensitive.Segmented;
import com.maybeitssquid.tin.InvalidTINException;
import com.maybeitssquid.tin.NationalTIN;

import java.util.Locale;

abstract public class UsTIN extends Segmented<CharSequence> implements NationalTIN {
    public UsTIN(final String... raw) {
        super(raw);
    }

    public static UsTIN create(final CharSequence raw) {
        return create(raw, false);
    }

    @Override
    public Locale issuer() {
        return Locale.US;
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
}
