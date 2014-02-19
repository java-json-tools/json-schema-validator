/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
