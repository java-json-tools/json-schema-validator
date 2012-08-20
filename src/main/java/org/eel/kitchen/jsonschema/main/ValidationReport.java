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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import org.eel.kitchen.jsonschema.util.JsonPointer;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class ValidationReport
{
    private JsonPointer path;
    private final ListMultimap<JsonPointer, String> msgMap
        = ArrayListMultimap.create();

    public ValidationReport()
    {
        try {
            path = new JsonPointer("");
        } catch (JsonSchemaException e) {
            // cannot happen
            throw new RuntimeException("WTF??");
        }
    }
    public ValidationReport(final JsonPointer path)
    {
        this.path = path;
    }

    public JsonPointer getPath()
    {
        return path;
    }

    public void setPath(final JsonPointer path)
    {
        this.path = path;
    }

    public void addMessage(final String message)
    {
        msgMap.put(path, message);
    }

    public void addMessages(final List<String> messages)
    {
        msgMap.putAll(path, messages);
    }

    public boolean isSuccess()
    {
        return msgMap.isEmpty();
    }

    public void mergeWith(final ValidationReport other)
    {
        msgMap.putAll(other.msgMap);
    }

    public ValidationReport copy()
    {
        return new ValidationReport(path);
    }

    public List<String> getMessages()
    {
        final SortedSet<JsonPointer> paths
            = new TreeSet<JsonPointer>(msgMap.keySet());

        final ImmutableList.Builder<String> builder = ImmutableList.builder();

        for (final JsonPointer path: paths)
            for (final String msg: msgMap.get(path)) {
                builder.add(path + ": " + msg);
            }

        return builder.build();
    }
}
