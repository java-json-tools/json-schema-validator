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

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.KeywordValidatorFactory;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import java.util.LinkedList;
import java.util.List;

public final class ArrayValidator
    extends ContainerValidator
{
    private List<JsonNode> items;

    private JsonNode additionalItems;

    public ArrayValidator(final Validator validator,
        final ValidationContext context, final JsonNode instance)
    {
        super(validator, context, instance);
    }

    @Override
    protected void buildPathProvider()
    {
        items = new LinkedList<JsonNode>();

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
    protected JsonNode getSchema(final String path)
    {
        final int index = Integer.parseInt(path);

        return index < items.size() ? items.get(index) : additionalItems;
    }

    @Override
    protected void buildQueue()
    {
        final KeywordValidatorFactory factory = context.getKeywordFactory();

        int i = 0;
        String subPath;
        JsonNode subSchema;
        ValidationContext ctx;
        Validator v;

        for (final JsonNode element: instance) {
            subPath = Integer.toString(i++);
            subSchema = getSchema(subPath);
            ctx = context.createContext(subPath, subSchema);
            v = factory.getValidator(ctx, element);
            queue.add(v);
        }
    }
}
