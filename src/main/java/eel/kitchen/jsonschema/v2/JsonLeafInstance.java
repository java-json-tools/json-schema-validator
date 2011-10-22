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

public final class JsonLeafInstance
    implements JsonInstance
{
    private final JsonNode node;

    public JsonLeafInstance(final JsonNode instance)
    {
        node = instance;
    }

    @Override
    public boolean accept(final JsonValidator validator)
    {
        return validator.visit(this);
    }

    @Override
    public NodeType getNodeType()
    {
        return NodeType.getNodeType(node);
    }

    @Override
    public JsonNode getInstance()
    {
        return node;
    }
}
