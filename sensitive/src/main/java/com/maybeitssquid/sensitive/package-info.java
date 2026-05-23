/**
 * Core framework for protecting sensitive data from accidental disclosure through string
 * formatting.
 *
 * <p>The primary entry point is {@link com.maybeitssquid.sensitive.Sensitive}, which wraps
 * sensitive data and controls how it's rendered via the {@link java.util.Formattable} interface.
 *
 * <h2>Quick Start</h2>
 *
 * <pre>{@code
 * // Wrap sensitive data - safe by default
 * Sensitive<String> secret = new Sensitive<>("my-secret");
 * System.out.println(secret);        // prints ""
 * System.out.printf("%s%n", secret); // prints ""
 *
 * // Subclass to add masking; see Renderers for renderer options
 * public class MaskedValue extends Sensitive<String> {
 *     private static final Renderer<String> RENDERER = Renderers.mask();
 *     public MaskedValue(String value) { super(value); }
 *
 *     @Override
 *     protected Renderer<String> getRenderer() { return RENDERER; }
 * }
 *
 * MaskedValue masked = new MaskedValue("secret123");
 * System.out.printf("%s%n", masked);    // prints "#####t123"
 * System.out.printf("%.0s%n", masked);  // prints "#########" (fully masked)
 * }</pre>
 *
 * @see com.maybeitssquid.sensitive.Sensitive
 * @see com.maybeitssquid.sensitive.Renderers
 */
package com.maybeitssquid.sensitive;
