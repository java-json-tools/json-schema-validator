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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.schema.JsonSchema;

public final class JsonValidator
{
    private final JsonNode schemaNode;

    public JsonValidator(final JsonNode schemaNode)
    {
        this.schemaNode = schemaNode;
    }


    public ValidationReport validate(final JsonNode instance)
    {
        final ValidationReport ret = new ValidationReport();
        final JsonSchema schema = JsonSchema.fromNode(schemaNode);
        schema.validate(ret, instance);
        return ret;
    }

    public ValidationReport validate(final String path, final JsonNode instance)
    {
        final ObjectNode refNode = JsonNodeFactory.instance.objectNode();
        refNode.put("$ref", path);

        final JsonSchema schema = JsonSchema.fromNode(schemaNode, refNode);
        final ValidationReport ret = new ValidationReport();
        schema.validate(ret, instance);
        return ret;
    }
}
