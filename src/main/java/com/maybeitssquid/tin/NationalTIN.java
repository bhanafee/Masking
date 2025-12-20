package com.maybeitssquid.tin;

import java.util.Locale;

/**
 * Interface for national Taxpayer Identification Numbers issued by a country.
 * <p>
 * For comparison functions such as equals, only the country code should be considered; the language and variant
 * fields of the locale should be ignored. For hashCode, consider using only the hash of the number field. Hash
 * collisions between TIN numbers with different national issuers should be rare.
 * <p>
 * Preferably, the locale should be one of the constants that are named for the country in
 * {@link java.util.Locale}. If no constant is available, consider using
 * {@link java.util.Locale#of(String, String) java.util.Locale.of("", code)}.
 */
public interface NationalTIN extends TIN<Locale> {
}
