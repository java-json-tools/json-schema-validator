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

package com.github.fge.jsonschema.format.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.net.MediaType;

/**
 * {@code media-type} format attribute
 *
 * <p>This is not defined by the specification itself, however it is mentioned
 * into draft v3's link schema.</p>
 *
 * <p>Fortunately enough, Guava has {@link MediaType} to help us out.</p>
 */
public final class MediaTypeFormatAttribute
    extends FormatAttribute
{
    private static final FormatAttribute INSTANCE
        = new MediaTypeFormatAttribute();

    private MediaTypeFormatAttribute()
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
            MediaType.parse(value.textValue());
        } catch (IllegalArgumentException ignored) {
            final Message.Builder msg = newMsg(fmt).addInfo("value", value)
                .setMessage("value is not a valid media type");
            report.addMessage(msg.build());
        }
    }
}
