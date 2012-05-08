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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public abstract class JsonSchema
{
    private static final Map<JsonNode, JsonSchema> cache
        = Collections.synchronizedMap(new LRUMap<JsonNode, JsonSchema>(10, 50));

    static {
        cache.put(null, new InvalidJsonSchema("schema is null"));
    }

    public static JsonSchema fromNode(final JsonNode parent,
        final JsonNode node)
    {
        final JsonNode schemaNode;

        try {
            schemaNode = JsonReference.resolveRef(parent, node);
        } catch (IOException e) {
            return new InvalidJsonSchema(e.getMessage());
        } catch (JsonSchemaException e) {
            return new InvalidJsonSchema(e.getMessage());
        }

        JsonSchema ret;

        synchronized (cache) {
            ret = cache.get(schemaNode);

            if (ret != null)
                return ret;

            ret = schemaNode.isObject()
                ? new ValidJsonSchema(parent, schemaNode)
                : new InvalidJsonSchema("schema is not an object");

            cache.put(schemaNode, ret);
        }

        return ret;
    }

    public static JsonSchema fromNode(final JsonNode node)
    {
        return fromNode(node, node);
    }

    public abstract void validate(final ValidationReport report,
        final JsonNode instance);
}
