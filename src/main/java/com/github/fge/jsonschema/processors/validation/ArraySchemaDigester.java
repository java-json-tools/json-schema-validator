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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.util.Digester;
import com.github.fge.jsonschema.util.NodeType;

public final class ArraySchemaDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new ArraySchemaDigester();

    static Digester getInstance()
    {
        return INSTANCE;
    }

    private ArraySchemaDigester()
    {
        super("", NodeType.ARRAY);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.put("itemsSize", 0);
        ret.put("itemsIsArray", false);

        final JsonNode itemsNode = schema.path("items");
        final JsonNode additionalNode = schema.path("additionalItems");

        final boolean hasItems = !itemsNode.isMissingNode();
        final boolean hasAdditional = additionalNode.isObject();

        ret.put("hasItems", hasItems);
        ret.put("hasAdditional", hasAdditional);

        if (itemsNode.isArray()) {
            ret.put("itemsIsArray", true);
            ret.put("itemsSize", itemsNode.size());
        }

        return ret;
    }
}
