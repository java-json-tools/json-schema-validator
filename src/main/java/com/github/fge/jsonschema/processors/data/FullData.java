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

package com.github.fge.jsonschema.processors.data;

import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;

import javax.annotation.concurrent.Immutable;

/**
 * Validation data for a validation processor
 *
 * <p>The included data are the schema (in the shape of a {@link SchemaTree} and
 * the instance to validate (in the shape of a {@link JsonTree}.</p>
 *
 * <p>The {@link ProcessingMessage} template generated contains information
 * about both the schema and instance.</p>
 */
@Immutable
public final class FullData
    implements MessageProvider
{
    private final SchemaTree schema;
    private final JsonTree instance;

    public FullData(final SchemaTree schema, final JsonTree instance)
    {
        this.schema = schema;
        this.instance = instance;
    }

    public FullData(final SchemaTree schema)
    {
        this(schema, null);
    }

    public SchemaTree getSchema()
    {
        return schema;
    }

    public JsonTree getInstance()
    {
        return instance;
    }

    /**
     * Return a new full data with another schema
     *
     * @param schema the schema
     * @return a new full data instance
     */
    public FullData withSchema(final SchemaTree schema)
    {
        return new FullData(schema, instance);
    }

    /**
     * Return a new full data with another instance
     *
     * @param instance the new instance
     * @return a new full data instance
     */
    public FullData withInstance(final JsonTree instance)
    {
        return new FullData(schema, instance);
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
