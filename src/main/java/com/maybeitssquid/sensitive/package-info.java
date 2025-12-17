/**
 * Core framework for protecting sensitive data from accidental disclosure through string formatting.
 *
 * <p>The primary entry point is {@link com.maybeitssquid.sensitive.Sensitive}, which wraps
 * sensitive data and controls how it's rendered via the {@link java.util.Formattable} interface.
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * // Wrap sensitive data
 * Sensitive<String> ssn = new Sensitive<>("123-45-6789");
 *
 * // Safe by default - prints empty string
 * System.out.println(ssn); // prints ""
 *
 * // With rendering control
 * Renderer<String> renderer = Renderers.simple(
 *     Extractor.string(),
 *     RegexRedactors.DEFAULT_MASK
 * );
 * Sensitive<String> maskedSSN = new Sensitive<>(renderer, "123-45-6789");
 * System.out.printf("%.4s", maskedSSN); // prints "###-##-6789"
 * }</pre>
 *
 * @see com.maybeitssquid.sensitive.Sensitive
 * @see com.maybeitssquid.sensitive.Renderers
 * @see com.maybeitssquid.sensitive.RegexRedactors
 */
package com.maybeitssquid.sensitive;