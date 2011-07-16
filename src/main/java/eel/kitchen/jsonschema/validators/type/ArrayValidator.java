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

package eel.kitchen.jsonschema.validators.type;

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.ArraySchemaProvider;
import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

public final class ArrayValidator
    extends AbstractValidator
{
    private int minItems = 0, maxItems = Integer.MAX_VALUE;
    private boolean uniqueItems = false;
    private final List<JsonNode> items = new LinkedList<JsonNode>();
    private boolean itemsTuples = false;
    private boolean additionalItemsOK = true;
    private JsonNode additionalItems = EMPTY_SCHEMA;

    public ArrayValidator()
    {
        registerField("minItems", NodeType.INTEGER);
        registerField("maxItems", NodeType.INTEGER);
        registerField("uniqueItems", NodeType.BOOLEAN);
        registerField("items", NodeType.OBJECT);
        registerField("items", NodeType.ARRAY);
        registerField("additionalItems", NodeType.BOOLEAN);
        registerField("additionalItems", NodeType.OBJECT);
    }

    @Override
    protected boolean doSetup()
    {
        JsonNode node;

        node = schema.get("minItems");
        if (node != null) {
            if (!node.isInt()) {
                messages.add("minItems overflow");
                return false;
            }
            minItems = node.getIntValue();
            if (minItems < 0) {
                messages.add("minItems is negative");
                return false;
            }
        }

        node = schema.get("maxItems");
        if (node != null) {
            if (!node.isInt()) {
                messages.add("maxItems overflow");
                return false;
            }
            maxItems = node.getIntValue();
            if (maxItems < 0) {
                messages.add("maxItems is negative");
                return false;
            }
        }

        if (maxItems < minItems) {
            messages.add("minItems is greater than maxItems");
            return false;
        }

        uniqueItems = schema.path("uniqueItems").getValueAsBoolean();

        return computeItems() && computeAdditionalItems() && finalCheck();
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final int nrItems = node.size();

        if (nrItems < minItems) {
            messages.add("array has less than minItems elements");
            return false;
        }

        if (nrItems > maxItems) {
            messages.add("array has more than maxItems elements");
            return false;
        }

        if (!additionalItemsOK && itemsTuples && nrItems > items.size()) {
            messages.add("array has extra elements, which the schema disallows");
            return false;
        }

        if (!uniqueItems)
            return true;

        try {
            CollectionUtils.toSet(node.getElements(), false);
        } catch (IllegalArgumentException e) {
            messages.add("items in the array are not unique");
            return false;
        }

        return true;
    }

    private boolean computeItems()
    {
        final JsonNode node = schema.get("items");

        if (node == null)
            return true;

        if (node.isObject()) {
            additionalItems = node;
            return true;
        }

        itemsTuples = true;

        for (final JsonNode element: node) {
            if (!element.isObject()) {
                messages.add("members of the items array should be objects");
                return false;
            }
            items.add(element);
        }

        return true;
    }

    private boolean computeAdditionalItems()
    {
        final JsonNode node = schema.get("additionalItems");

        if (node == null)
            return true;

        if (node.isBoolean()) {
            additionalItemsOK = node.getBooleanValue();
            return true;
        }

        additionalItems = node;
        return true;
    }

    private boolean finalCheck()
    {
        if (!itemsTuples)
            return true;

        if (additionalItemsOK)
            return true;

        if (minItems <= items.size())
            return true;

        messages.add("minItems is greater than what the schema allows "
            + "(tuples, additional)");
        return false;
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ArraySchemaProvider(items, additionalItems);
    }

}
