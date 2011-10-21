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

import org.codehaus.jackson.JsonNode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public final class CombinedSchema
    implements Iterator<JsonNode>
{
    private final Queue<JsonNode> queue = new ArrayDeque<JsonNode>();

    public CombinedSchema(final JsonNode... schemas)
    {
        queue.addAll(Arrays.asList(schemas));
    }

    @Override
    public boolean hasNext()
    {
        return !queue.isEmpty();
    }

    @Override
    public JsonNode next()
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
