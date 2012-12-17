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

package org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.syntax.AbstractSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;

/**
 * Syntax validator for the {@code mediaType} hyper-schema keyword
 *
 * <p>The only requirement is that it be a media type as defined by RFC 2045 and
 * 2046. Guava helps here since it has {@link MediaType}, which we use here.</p>
 */
public final class MediaTypeSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new MediaTypeSyntaxChecker();

    private MediaTypeSyntaxChecker()
    {
        super("mediaType", NodeType.STRING);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final Message.Builder msg, final List<Message> messages,
        final JsonNode schema)
    {
        final String input = schema.get(keyword).textValue();

        try {
            MediaType.parse(input);
        } catch (IllegalArgumentException ignored) {
            msg.setMessage("incorrect media type").addInfo("value", input);
            messages.add(msg.build());
        }
    }
}
