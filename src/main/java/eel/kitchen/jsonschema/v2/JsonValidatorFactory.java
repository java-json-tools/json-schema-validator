/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;

public final class JsonValidatorFactory
{
    private static final JsonValidatorFactory instance
        = new JsonValidatorFactory();

    private JsonValidatorFactory()
    {
    }

    public static JsonValidatorFactory getInstance()
    {
        return instance;
    }

    public JsonValidator getValidator(final JsonNode schema)
    {
        if (schema == null)
            return failure("schema is null");

        if (!schema.isObject())
            return failure("JSON document is not a schema");

        if (schema.has("$ref"))
            return failure("Sorry, $ref not implemented yet");

        if (schema.has("extends"))
            return failure("Sorry, extends not implemented yet");

        if (!schema.has("type"))
            return failure("Sorry, I need a type element for now");

        final JsonNode typeNode = schema.get("type");

        if (!typeNode.isTextual())
            return failure("Sorry, I only support simple types for now");

        final String typeName = typeNode.getTextValue();

        try {
            final NodeType type = NodeType.valueOf(typeName.toUpperCase());
            return new JsonLeafValidator(type, schema);
        } catch (IllegalArgumentException e) {
            return failure("unknown type " + typeName);
        }
    }

    private static JsonValidator failure(final String message)
    {
        return new JsonValidator()
        {
            @Override
            public boolean visit(final JsonInstance instance)
            {
                return false;
            }

            @Override
            public List<String> getMessages()
            {
                return Arrays.asList(message);
            }
        };
    }
}
