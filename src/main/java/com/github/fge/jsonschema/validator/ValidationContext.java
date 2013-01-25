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

package com.github.fge.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.schema.SchemaContext;
import com.github.fge.jsonschema.schema.SchemaNode;
import com.google.common.collect.Queues;

import java.util.Deque;

/**
 * A validation context
 *
 * <p>This object is passed along the validation process. At any point in the
 * validation process, it contains the current schema context, the feature set
 * and the validator cache.</p>
 *
 * <p>The latter is necessary since four keywords may have to spawn other
 * validators: {@code type}, {@code disallow}, {@code dependencies} and {@code
 * extends}.</p>
 *
 * <p>One instance is created for each validation and is passed around to all
 * validators. Due to this particular usage, it is <b>not</b> thread safe.</p>
 */
public final class ValidationContext
{
    private final JsonValidatorCache cache;
    private final Deque<SchemaContext> contextQueue = Queues.newArrayDeque();

    /**
     * Create a validation context with an empty feature set
     *
     * @param cache the validator cache to use
     */
    public ValidationContext(final JsonValidatorCache cache)
    {
        this.cache = cache;
    }

    void pushContext(final SchemaContext context)
    {
        contextQueue.addFirst(context);
    }

    void popContext()
    {
        contextQueue.removeFirst();
    }

    /**
     * Return a format attribute for a given attribute
     *
     * @param fmt the format attribute
     * @return the attribute, {@code null} if not found
     */
    public FormatAttribute getFormat(final String fmt)
    {
        return cache.getFormatAttributes().get(fmt);
    }

    /**
     * Build a new validator out of a JSON document
     *
     * <p>This calls {@link JsonValidatorCache#getValidator(SchemaNode)} with
     * this context's {@link SchemaContext} used as a schema context.</p>
     *
     * @param node the node (a subnode of the schema)
     * @return a validator
     */
    public JsonValidator newValidator(final JsonNode node)
    {
        final SchemaContext schemaContext = contextQueue.getFirst();
        final SchemaNode schemaNode = new SchemaNode(schemaContext, node);
        return cache.getValidator(schemaNode);
    }

    @Override
    public String toString()
    {
        return "current: " + contextQueue.peekFirst();
    }
}
