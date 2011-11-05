/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.container;

import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

public final class ArrayValidator
    extends ContainerValidator
{
    private final List<JsonNode> items = new LinkedList<JsonNode>();

    private JsonNode additionalItems;

    public ArrayValidator(final Validator validator,
        final ValidationContext context, final JsonNode instance)
    {
        super(validator, context, instance);
    }

    @Override
    protected void buildPathProvider()
    {
        final JsonNode schema = context.getSchemaNode();

        JsonNode node = schema.path("items");

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray()) {
            for (final JsonNode item: node)
                items.add(item);
        }

        node = schema.path("additionalItems");

        additionalItems = node.isObject() ? node : EMPTY_SCHEMA;
    }

    @Override
    protected Validator getValidator(final String path, final JsonNode child)
    {
        final int index = Integer.parseInt(path);

        final JsonNode node = index < items.size() ? items.get(index)
            : additionalItems;

        final ValidationContext ctx = context.createContext(path, node);

        return ctx.getValidator(child);
    }

    @Override
    protected void buildQueue()
    {
        int i = 0;
        String path;
        Validator v;

        for (final JsonNode child: instance) {
            path = Integer.toString(i++);
            v = getValidator(path, child);
            queue.add(v);
        }
    }
}
