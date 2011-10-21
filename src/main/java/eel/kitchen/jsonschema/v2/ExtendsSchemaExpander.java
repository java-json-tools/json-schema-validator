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

import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public final class ExtendsSchemaExpander
    implements Iterator<CombinedSchema>
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private final Queue<CombinedSchema> queue = new ArrayDeque<CombinedSchema>();

    public ExtendsSchemaExpander(final JsonNode schema)
    {
        final ObjectNode other = factory.objectNode();

        other.putAll(CollectionUtils.toMap(schema.getFields()));

        final JsonNode extendsNode = other.remove("extends");

        if (extendsNode == null) {
            queue.add(new CombinedSchema(other));
            return;
        }

        if (extendsNode.isObject()) {
            queue.add(new CombinedSchema(extendsNode, other));
            return;
        }

        if (!extendsNode.isArray())
            throw new IllegalArgumentException("extends should be a schema or"
                + " an array of schemas");

        for (final JsonNode node: extendsNode) {
            if (!node.isObject())
                throw new IllegalArgumentException("array elements of extends"
                    + " should be schemas");
            queue.add(new CombinedSchema(node, other));
        }
    }
    @Override
    public boolean hasNext()
    {
        return !queue.isEmpty();
    }

    @Override
    public CombinedSchema next()
    {
        if (!hasNext())
            throw new NoSuchElementException();

        return queue.remove();
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
