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

package com.github.fge.jsonschema.format.common;

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Validator for the {@code uri} format attribute.
 *
 * <p>Note that each and any URI is allowed. In particular, it is not required
 * that the URI be absolute or normalized.</p>
 */
public final class URIAttribute
    extends AbstractFormatAttribute
{
    private static final FormatAttribute INSTANCE = new URIAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private URIAttribute()
    {
        super("uri", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final String value = data.getInstance().getNode().textValue();

        try {
            new URI(value);
        } catch (URISyntaxException ignored) {
            report.error(newMsg(data, bundle, "err.format.invalidURI")
                .putArgument("value", value));
        }
    }
}
