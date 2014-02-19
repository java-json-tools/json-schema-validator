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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.Lists;
import org.mockito.verification.VerificationMode;

import java.util.List;

import static org.mockito.Mockito.*;

public final class TestUtils
{
    private TestUtils()
    {
    }

    public static VerificationMode onlyOnce()
    {
        return times(1);
    }

    public static ProcessingReport anyReport()
    {
        return any(ProcessingReport.class);
    }

    public static SchemaTree anySchema()
    {
        return any(SchemaTree.class);
    }

    public static ProcessingMessage anyMessage()
    {
        return any(ProcessingMessage.class);
    }

    // TODO: while we're at it, the @DataProvider could be factorized!
    public static String buildMessage(final MessageBundle BUNDLE,
        final String key,
        final JsonNode params, final JsonNode data)
    {
        final ProcessingMessage message = new ProcessingMessage()
            .setMessage(BUNDLE.getMessage(key));
        if (params != null) {
            String name;
            JsonNode value;
            for (final JsonNode node: params) {
                name = node.textValue();
                value = data.get(name);
                message.putArgument(name, valueToArgument(value));
            }
        }
        return message.getMessage();
    }

    private static Object valueToArgument(final JsonNode value)
    {
        final NodeType type = NodeType.getNodeType(value);

        switch (type) {
            case STRING:
                return value.textValue();
            case INTEGER:
                return value.bigIntegerValue();
            case NUMBER: case NULL:
                return value;
            case BOOLEAN:
                return value.booleanValue();
            case ARRAY:
                final List<Object> list = Lists.newArrayList();
                for (final JsonNode element: value)
                    list.add(valueToArgument(element));
                return list;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
