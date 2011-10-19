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
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public final class JsonSubSchema
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private final NodeType type;

    private final JsonNode schema;

    public JsonSubSchema(final NodeType type, final JsonNode schema)
    {
        this.type = type;
        this.schema = schema;
    }

    @Override
    public int hashCode()
    {
        return getFullSchema().hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (!(obj instanceof JsonSubSchema))
            return false;
        return getFullSchema().equals(((JsonSubSchema) obj).getFullSchema());
    }

    private JsonNode getFullSchema()
    {
        final ObjectNode ret = factory.objectNode();

        ret.putAll((ObjectNode) schema);
        ret.put("type", type.toString());
        return ret;
    }
}
