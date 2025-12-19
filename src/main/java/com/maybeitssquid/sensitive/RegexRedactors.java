package com.maybeitssquid.sensitive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class RegexRedactors {

    /**
     * Default replacement character for sensitive values.
     */
    public static char DEFAULT_REPLACEMENT = '#';

    /**
     * The default mask redactor allows {@link Renderers#DEFAULT_DELIMITER} and replaces all other characters with
     * {@link #DEFAULT_REPLACEMENT} as needed to achieve the required precision.
     */
    public static final Renderers.Redactor DEFAULT_MASK = maskWith(DEFAULT_REPLACEMENT, Renderers.DEFAULT_DELIMITER);

    /**
     * Returns a redactor that deletes non-allowable characters to meet the required precision.
     * Examples:
     * <ul>
     *     <li>{@code truncate()} removes any character beyond what is needed to meet the required precision.</li>
     *     <li>{@code truncate('-', '/')} removes any character except '-' or '/' beyond what is needed to meet the
     *     required precision.</li>
     * </ul>
     *
     * @param allowable characters that are never truncated
     * @return redactor that deletes non-allowable characters
     */
    public static Renderers.Redactor truncate(final char... allowable) {
        return truncate(redactable(allowable));
    }

    /**
     * Returns a redactor that deletes segments that match the allowable regex to meet the required precision.
     * Examples:
     * <ul>
     *     <li>{@code truncate("\\d")} removes digits beyond what is needed to meet the required precision.</li>
     *     <li>{@code truncate("[0-9a-zA-Z]")} removes alphanumerics beyond what is needed to meet the
     *     required precision.</li>
     * </ul>
     *
     * @param redactable regex for segments that are truncated
     * @return redactor that deletes non-allowable segments
     */
    public static Renderers.Redactor truncate(final String redactable) {
        return redact("", redactable);
    }

    /**
     * Equivalent to {@code maskWith(DEFAULT_REPLACEMENT, allowable)}.
     * Examples:
     * <ul>
     *     <li>{@code mask()} replaces any character with the default replacement.</li>
     *     <li>{@code mask(DEFAULT_DELIMITER)} replaces any character except
     *     {@link Renderers#DEFAULT_DELIMITER} with the default replacement.</li>
     *     <li>{@code mask('-', '/')} replaces any character except '-' or '/' with the default replacement</li>
     * </ul>
     *
     * @param allowable characters that are passed through
     * @return redactor that redacts replaces characters up to the required precision with a fixed replacement
     * @see #maskWith(char, char...)
     */
    public static Renderers.Redactor mask(final char... allowable) {
        return maskWith(DEFAULT_REPLACEMENT, allowable);
    }

    /**
     * Equivalent to {@code maskWith(DEFAULT_REPLACEMENT, redactable)}.
     *
     * @param redactable regex for segments that are to be redacted
     * @return redactor that replaces segments.
     * @see #DEFAULT_REPLACEMENT
     * @see #maskWith(char, String)
     */
    public static Renderers.Redactor mask(final String redactable) {
        return maskWith(DEFAULT_REPLACEMENT, redactable);
    }

    /**
     * Returns a redactor that replaces the non-allowable characters with the {@link #DEFAULT_REPLACEMENT} as
     * needed to meet the required precision.
     * <p/>
     * Examples:
     * <ul>
     *     <li>{@code mask('#', '-')} replaces any character except hyphen with'#'.</li>
     *     <li>{@code mask('x', '-', '/')} replaces any character except '-' and '/' with 'x'
     *     <li>{@code mask(DEFAULT_REPLACEMENT, DEFAULT_DELIMITER)} replaces any character except the
     *     {@link Renderers#DEFAULT_DELIMITER} with the {@link #DEFAULT_REPLACEMENT}.</li>
     * </ul>
     *
     * @param replacement the replacement for each redacted character
     * @param allowable   characters that are passed through
     * @return redactor that replaces all but specific characters
     */
    public static Renderers.Redactor maskWith(final char replacement, final char... allowable) {
        return maskWith(replacement, redactable(allowable));
    }

    /**
     * Returns a redactor that replaces matching segments with a fixed character to meet the required precision.
     * <p/>
     * Examples:
     * <ul>
     *     <li>{@code redact('x', "\\d")} replaces digits with lower case 'x'</li>
     *     <li>{@code redact('*', "[a-z]")} replaces lower-case letters with '*'</li>
     *     <li>{@code redact('#', ".")} redacts all characters with '#'</li>
     * </ul>
     * In all of the above examples, redactions repeated as needed to meet the required precision.
     *
     * @param replacement the replacement for each redacted segment.
     * @param redactable  regex for segments that are to be redacted
     * @return redactor that replaces segments
     */
    public static Renderers.Redactor maskWith(final char replacement, final String redactable) {
        return redact(Character.toString(replacement), redactable);
    }

    /**
     * Builds a {@link Renderers.Redactor} based on regular expressions. Redactable ("blacklist") segments may be replaced,
     * depending upon the {@code precision} passed to the redactor at runtime.
     * <p/>
     * A value may be viewed as alternating non-redactable and redactable segments,
     * e.g. {@code (^redactable*)((redactable)(^redactable*))*}.
     * First, the number of redactable segments is counted and compared with the requested {@code precision} to
     * determine how many of the redactable segments to redact. That number of matching redactable segments are
     * replaced to produce the final output.
     * <p/>
     * Typically:
     * <ol>
     *     <li>The {@code redactable} regex is defined to match only a single character.</li>
     *     <li>The length of the replacement string is either 0 or 1 characters.</li>
     * </ol>
     * If neither an allowable nor a redactable pattern is specified, the default behavior is to consider every
     * character redactable.
     *
     * @param replacement replacement for redacted segments.
     * @param redactable  regular expression for segments that may be redacted.
     * @return a redactor that replaces the requested number of redactable segments with a fixed string.
     */
    public static Renderers.Redactor redact(final String replacement, final String redactable) {
        final Pattern pattern = Pattern.compile(redactable == null || redactable.isEmpty() ? "." : redactable);

        return (p, cs) -> {
            if (cs == null) return "";
            final Matcher m = pattern.matcher(cs);

            // count redactable segments
            int count = 0;
            while (m.find()) {
                count += 1;
            }
            final int redact = redactions(p, count);

            m.reset();
            final StringBuilder sb = new StringBuilder(cs.length());
            IntStream.range(0, redact).forEach(i -> {
                if (m.find()) {
                    m.appendReplacement(sb, replacement);
                }
            });
            m.appendTail(sb);
            return sb.toString();
        };
    }

    /**
     * Generates a regex to match any single character except those provided.
     *
     * @param allowable the characters that do not match
     * @return regex that matches any single non-allowable character
     */
    private static String redactable(char... allowable) {
        if (allowable == null || allowable.length == 0) {
            return ".";
        } else {
            return "[^" + Pattern.quote(String.valueOf(allowable)) + "]";
        }
    }

    /**
     * Calculate the number of redactions to perform, based on showing {@code precision} of a total of {@code count}
     * available.
     *
     * @param precision the requested precision. If this value is {@code -1}, the precision is calculated to show
     *                  one half, rounding down.
     * @param count     the number of potential redactions.
     * @return the number of redactions to perform.
     */
    public static int redactions(final int precision, final int count) {
        if (precision == -1) {
            return (count + 1) / 2;
        } else if (precision < count) {
            return count - precision;
        } else {
            return 0;
        }
    }
}
