package com.maybeitssquid.tin;

import java.util.Formattable;

/**
 * Interface for Taxpayer Identification Numbers.
 *
 * @param <I> the type of the issuer
 */
public interface TIN<I> extends Formattable {

    /**
     * Returns the issuer of this TIN.
     *
     * @return the issuer
     */
    I issuer();
}