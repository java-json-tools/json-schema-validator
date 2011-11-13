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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.container;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.AdditionalItemsKeywordValidator;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link ContainerValidator} for array instances
 *
 * @see ContainerValidator
 */
public final class ArrayValidator
    extends ContainerValidator
{
    /**
     * The elements in {@code items}, if any
     */
    private final List<JsonNode> items = new LinkedList<JsonNode>();

    /**
     * What is in {@code additionalItems}, or {@code items} if the latter
     * contains a schema
     */
    private JsonNode additionalItems;

    public ArrayValidator(final Validator validator)
    {
        super(validator);
    }

    /**
     * <p>Builds the {@link #items} list and {@link #additionalItems} schema,
     * with the following algorithm:</p>
     * <ul>
     *     <li>if {@code items} is a schema, leave the items array empty and
     *     affect the value of the node to {@link #additionalItems};
     *     </li>
     *     <li>if it is an array, fill the {@link #items} list with the array
     *     elements; then fill {@link #additionalItems}.</li>
     * </ul>
     * <p>Note that at this stage, as the structure has been validated,
     * it means that {@link AdditionalItemsKeywordValidator} will have done its
     * job and make validation fail if additional items were not permitted.
     * </p>
     */
    @Override
    protected void buildPathProvider(final JsonNode schema)
    {
        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray())
            for (final JsonNode item: node)
                items.add(item);

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    protected Collection<JsonNode> getSchemas(final String path)
    {
        final int index = Integer.parseInt(path);

        final JsonNode schema =  index < items.size() ? items.get(index)
            : additionalItems;

        return Arrays.asList(schema);
    }

    @Override
    protected ValidationReport validateChildren(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();

        int i = 0;
        String path;
        ValidationContext ctx;
        JsonNode schema;
        Validator v;

        for (final JsonNode child: instance) {
            path = Integer.toString(i++);
            schema = getSchemas(path).iterator().next();
            ctx = context.createContext(path, schema);
            v = ctx.getValidator(instance);
            report.mergeWith(v.validate(ctx, child));
        }

        return report;
    }
}
