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
import com.github.fge.jsonschema.tree.JsonTree2;

/**
 * Validation data for a validation processor
 *
 * <p>The included data are the schema (in the shape of a {@link JsonSchemaTree}
 * and the instance to validate (in the shape of a {@link JsonTree}.</p>
 */
public final class ValidationData
    implements MessageProvider
{
    private final JsonSchemaTree schema;
    private final JsonTree2 instance;

    public ValidationData(final JsonSchemaTree schema, final JsonTree2 instance)
    {
        this.schema = schema;
        this.instance = instance;
    }

    public ValidationData(final JsonSchemaTree schema)
    {
        this(schema, null);
    }

    public JsonSchemaTree getSchema()
    {
        return schema;
    }

    public JsonTree2 getInstance()
    {
        return instance;
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
