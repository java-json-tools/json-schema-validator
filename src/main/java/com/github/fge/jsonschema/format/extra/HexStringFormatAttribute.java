package com.github.fge.jsonschema.format.extra;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
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

        report.error(newMsg(data, bundle, "err.format.hexString.illegalChars"));
    }
}
