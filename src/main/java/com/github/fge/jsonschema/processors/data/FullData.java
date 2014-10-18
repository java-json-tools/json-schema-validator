/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.processors.data;


import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;

import javax.annotation.concurrent.Immutable;

/**
 * Validation data for a validation processor
 *
 * <p>The included data are the schema (in the shape of a {@link SchemaTree},
 * the instance to validate (in the shape of a {@link JsonTree} and a boolean
 * indicating whether validation should go as deep as posssible.</p>
 *
 * <p>If the boolean argument is false, then container children (array elements
 * or object members) will not be validated if the container itself fails
 * validation.</p>
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
    private final boolean deepCheck;

    public FullData(final SchemaTree schema, final JsonTree instance,
        final boolean deepCheck)
    {
        this.schema = schema;
        this.instance = instance;
        this.deepCheck = deepCheck;
    }

    public FullData(final SchemaTree schema, final JsonTree instance)
    {
        this(schema, instance, false);
    }

    @Deprecated
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

    public boolean isDeepCheck()
    {
        return deepCheck;
    }

    /**
     * Return a new full data with another schema
     *
     * @param schema the schema
     * @return a new full data instance
     */
    public FullData withSchema(final SchemaTree schema)
    {
        return new FullData(schema, instance, deepCheck);
    }

    /**
     * Return a new full data with another instance
     *
     * @param instance the new instance
     * @return a new full data instance
     */
    public FullData withInstance(final JsonTree instance)
    {
        return new FullData(schema, instance, deepCheck);
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
