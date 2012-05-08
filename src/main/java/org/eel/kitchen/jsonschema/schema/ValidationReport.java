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
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.util.JsonPointer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ValidationReport
{
    private JsonNode schema;
    private JsonPointer path;
    private final List<String> messages = new LinkedList<String>();

    public ValidationReport asNew()
    {
        final ValidationReport ret = new ValidationReport();

        ret.path = path;

        return ret;
    }
    public ValidationReport()
    {
        try {
            path = new JsonPointer("#");
        } catch (JsonSchemaException e) {
            throw new RuntimeException("WTF??", e);
        }
    }

    public void addMessage(final String message)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(path).append(": ");
        sb.append(message);
        messages.add(sb.toString());
    }

    public void setPath(final JsonPointer path)
    {
        this.path = path;
    }

    public JsonPointer getPath()
    {
        return path;
    }

    public void setSchema(final JsonNode schema)
    {
        this.schema = schema;
    }

    public JsonNode getSchema()
    {
        return schema;
    }

    public boolean isSuccess()
    {
        return messages.isEmpty();
    }

    public void mergeWith(final ValidationReport other)
    {
        messages.addAll(other.messages);
    }

   public List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
