/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.container;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;

import java.util.Map;
import java.util.SortedMap;

/**
 * A specialized {@link ContainerValidator} for object nodes.
 */
public final class ObjectValidator
    extends ContainerValidator
{
    private final ObjectSchemaNode schema;

    public ObjectValidator(final JsonNode schemaNode, final Validator validator)
    {
        super(validator);

        schema = new ObjectSchemaNode(schemaNode);
    }

    @Override
    protected ValidationReport validateChildren(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();

        final SortedMap<String, JsonNode> map
            = CollectionUtils.toSortedMap(instance.fields());

        String path;
        JsonNode child;
        ValidationContext ctx;
        Validator v;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            path = entry.getKey();
            child = entry.getValue();
            for (final JsonNode node: schema.objectPath(path)) {
                ctx = context.relocate(path, node);
                v = ctx.getValidator(child);
                report.mergeWith(v.validate(ctx, child));
            }
        }

        return report;
    }
}
