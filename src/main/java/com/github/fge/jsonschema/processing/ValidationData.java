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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;

public final class ValidationData
    implements MessageProvider
{
    private JsonSchemaTree schema;
    private JsonTree instance;

    public JsonSchemaTree getSchema()
    {
        return schema;
    }

    public void setSchema(final JsonSchemaTree schema)
    {
        this.schema = schema;
    }

    public JsonTree getInstance()
    {
        return instance;
    }

    public void setInstance(final JsonTree instance)
    {
        this.instance = instance;
    }

    @Override
    public ProcessingMessage newMessage()
    {
        final ProcessingMessage ret = new ProcessingMessage();
        if (schema != null)
            ret.put("schema", schema);
        if (instance != null)
            ret.put("instance", instance);
        return ret;
    }
}
