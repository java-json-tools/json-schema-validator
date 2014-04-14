/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.format.extra;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.CharMatcher;

import java.util.regex.Pattern;

/**
 * Format specifier for an hypothetical {@code base64} format attribute
 *
 * <p>This implements Base64 as defined in RFC 4648 with one difference: while
 * the RFC states that excess padding characters ({@code =}) MAY be ignored, it
 * is chosen here to require that there be at most two, as per Base64 encoding
 * rules.</p>
 */
public final class Base64FormatAttribute
    extends AbstractFormatAttribute
{
    /*
     * The algorithm is as follows:
     *
     * * first and foremost, check whether the total length of the input string
     *   is a multiple of 4: even though the RFC does not state this explicitly,
     *   it is obvious enough that this must be the case anyway;
     * * if this check succeeds, remove _at most two_ trailing '=' characters;
     * * check that, after this removal, all remaining characters are within the
     *   Base64 alphabet, as defined by the RFC.
     */

    /*
     * Regex to accurately remove _at most two_ '=' characters from the end of
     * the input.
     */
    private static final Pattern PATTERN = Pattern.compile("==?$");

    /*
     * Negation of the Base64 alphabet. We try and find one character, if any,
     * matching this "negated" character matcher.
     *
     * FIXME: use .precomputed()?
     */
    private static final CharMatcher NOT_BASE64
        = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'))
            .or(CharMatcher.inRange('0', '9')).or(CharMatcher.anyOf("+/"))
            .negate();

    private static final FormatAttribute instance
        = new Base64FormatAttribute();

    public static FormatAttribute getInstance()
    {
        return instance;
    }

    private Base64FormatAttribute()
    {
        super("base64", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String input = data.getInstance().getNode().textValue();

        /*
         * The string length must be a multiple of 4. FIXME though: can it be 0?
         * Here, it is assumed that it can, even though that does not really
         * make sense.
         */
        if (input.length() % 4 != 0) {
            report.error(newMsg(data, bundle, "err.format.base64.badLength")
                .putArgument("length", input.length()));
            return;
        }

        final int index
            = NOT_BASE64.indexIn(PATTERN.matcher(input).replaceFirst(""));

        if (index == -1)
            return;

        report.error(newMsg(data, bundle, "err.format.base64.illegalChars")
            .putArgument("character", Character.toString(input.charAt(index)))
            .putArgument("index", index));
    }
}
