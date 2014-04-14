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

package com.github.fge.jsonschema.format.helpers;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.CharMatcher;

/**
 * Base class for hexadecimal string-based representations
 *
 * <p>This class is particularly useful to validate hexadecimal-based string
 * data. The only two constructor arguments you have to specify are a short
 * description of the validated string and its expected length.</p>
 *
 * <p>In this package, it is used for validating MD5, SHA1, SHA256 and SHA512
 * checksums, which are very commonly represented in form of hexadecimal strings
 * of fixed length.</p>
 */
public abstract class HexStringFormatAttribute
    extends AbstractFormatAttribute
{
    // FIXME: maybe there is a better way to do that? CharMatcher does not seem
    // to have the following predefined...
    private static final CharMatcher HEX_CHARS
        = CharMatcher.anyOf("0123456789abcdefABCDEF").precomputed();

    protected final int length;

    protected HexStringFormatAttribute(final String fmt, final int length)
    {
        super(fmt, NodeType.STRING);
        this.length = length;
    }

    @Override
    public final void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String input = data.getInstance().getNode().textValue();

        if (length != input.length()) {
            report.error(newMsg(data, bundle, "err.format.hexString.badLength")
                .putArgument("actual", input.length())
                .putArgument("expected", length));
            return;
        }

        if (HEX_CHARS.matchesAllOf(input))
            return;

        final int index = HEX_CHARS.negate().indexIn(input);

        report.error(newMsg(data, bundle, "err.format.hexString.illegalChar")
            .putArgument("character", Character.toString(input.charAt(index)))
            .putArgument("index", index));
    }
}
