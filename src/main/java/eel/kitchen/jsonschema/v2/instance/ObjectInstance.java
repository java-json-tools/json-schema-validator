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

package eel.kitchen.jsonschema.v2.instance;

import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

final class ObjectInstance
    extends ContainerInstance
{
    ObjectInstance(final String pathElement, final JsonNode node)
    {
        super(pathElement, node, NodeType.OBJECT);
    }

    @Override
    protected void buildChildren()
    {
        children = new HashSet<Instance>();

        final Map<String, JsonNode> map = CollectionUtils.toMap(node
            .getFields());

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            children.add(InstanceFactory.buildInstance(entry.getKey(),
                entry.getValue()));

        children = Collections.unmodifiableSet(children);
    }
}
