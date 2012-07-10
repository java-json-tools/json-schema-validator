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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.LRUMap;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.ref.JsonReference;

import java.util.Map;

public abstract class AbstractJsonValidator
    implements JsonValidator
{
    private static final Map<JsonNode, JsonValidator> cache
        = new LRUMap<JsonNode, JsonValidator>(10, 50);

    private static final SyntaxValidator syntaxValidator
        = new SyntaxValidator();

    public static JsonValidator fromNode(final JsonNode parent,
        final JsonNode node)
    {
        final JsonNode schemaNode;

        try {
            schemaNode = JsonReference.resolveRef(parent, node);
        } catch (JsonSchemaException e) {
            return new InvalidJsonValidator(e.getMessage());
        }

        JsonValidator ret;

        synchronized (cache) {
            ret = cache.get(schemaNode);

            if (ret != null)
                return ret;

            final ValidationContext context = new ValidationContext();
            syntaxValidator.validate(context, schemaNode);

            if (!context.isSuccess())
                return new InvalidJsonValidator(context);

            ret = schemaNode.isObject()
                ? new ValidJsonValidator(parent, schemaNode)
                : new InvalidJsonValidator("schema is not an object");

            cache.put(schemaNode, ret);
        }

        return ret;
    }

    public static JsonValidator fromNode(final JsonNode node)
    {
        return fromNode(node, node);
    }

    public abstract void validate(final ValidationContext context,
        final JsonNode instance);
}
