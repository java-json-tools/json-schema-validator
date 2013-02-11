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

package com.github.fge.jsonschema.keyword.digest.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.util.Digester;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class DraftV3PropertiesDigester
    extends AbstractDigester
{
    private static final Digester INSTANCE = new DraftV3PropertiesDigester();

    public static Digester getInstance()
    {
        return INSTANCE;
    }

    private DraftV3PropertiesDigester()
    {
        super("properties", NodeType.OBJECT);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        // TODO: return an array directly (same for "required" in v4)
        final ObjectNode ret = FACTORY.objectNode();
        final ArrayNode required = FACTORY.arrayNode();
        ret.put("required", required);

        final JsonNode node = schema.get(keyword);
        final List<String> list = Lists.newArrayList(node.fieldNames());

        Collections.sort(list);

        for (final String field: list)
            if (node.get(field).path("required").asBoolean(false))
                required.add(field);

        return ret;
    }
}
