/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.old.format.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.old.format.FormatAttribute;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Validator for the {@code uri} format attribute.
 *
 * <p>Note that each and any URI is allowed. In particular, it is not required
 * that the URI be absolute or normalized.</p>
 */
public final class URIFormatAttribute
    extends FormatAttribute
{
    private static final FormatAttribute INSTANCE
        = new URIFormatAttribute();

    private URIFormatAttribute()
    {
        super(NodeType.STRING);
    }

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final String fmt, final ValidationReport report,
        final JsonNode value)
    {
        try {
            new URI(value.textValue());
        } catch (URISyntaxException ignored) {
            final Message.Builder msg = newMsg(fmt).addInfo("value", value)
                .setMessage("string is not a valid URI");
            report.addMessage(msg.build());
        }
    }
}
