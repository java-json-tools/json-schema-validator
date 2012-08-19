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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.ValidationContext;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.Set;

/**
 * Validator for the {@code extends} keyword
 *
 * <p>This only really triggers schema validations.</p>
 */
public final class ExtendsKeywordValidator
    extends KeywordValidator
{
    private final Set<JsonNode> schemas;

    public ExtendsKeywordValidator(final JsonNode schema)
    {
        super(NodeType.values());
        final JsonNode node = schema.get("extends");
        final ImmutableSet.Builder<JsonNode> builder
            = new ImmutableSet.Builder<JsonNode>();

        /*
         * Again, the fact that syntax validation has ensured our schema's
         * correctness helps greatly: the keyword value is either an object
         * or an array of objects.
         *
         * If this is an array, just cycle through its elements and stuff
         * them in our schema set. It should be noted that the draft DOES NOT
         * require that elements in the array must be unique,
         * but we swallow duplicates this way.
         */

        if (node.isObject())
            builder.add(node);
        else
            builder.addAll(node);

        schemas = builder.build();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final SchemaContainer container = context.getContainer();
        final JsonSchemaFactory factory = context.getFactory();
        JsonSchema subSchema;

        for (final JsonNode schema: schemas) {
            subSchema = factory.create(container, schema);
            subSchema.validate(context, report, instance);
        }
    }
}
