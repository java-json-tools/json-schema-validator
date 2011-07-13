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

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.ArraySchemaProvider;
import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

public final class ArrayValidator
    extends AbstractTypeValidator
{
    private int minItems = 0, maxItems = Integer.MAX_VALUE;
    private boolean uniqueItems = false;
    private final List<JsonNode> items = new LinkedList<JsonNode>();
    private boolean itemsTuples = false;
    private boolean additionalItemsOK = true;
    private JsonNode additionalItems = EMPTY_SCHEMA;

    public ArrayValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        JsonNode node;

        node = schemaNode.get("minItems");
        if (node != null) {
            if (!node.isInt())
                throw new MalformedJasonSchemaException("minItems should be " +
                    "an integer");
            minItems = node.getIntValue();
        }

        node = schemaNode.get("maxItems");
        if (node != null) {
            if (!node.isInt())
                throw new MalformedJasonSchemaException("maxItems should be " +
                    "an integer");
            maxItems = node.getIntValue();
        }

        if (maxItems < minItems)
            throw new MalformedJasonSchemaException("minItems should be less" +
                " than or equal to maxItems");

        node = schemaNode.get("uniqueItems");
        if (node != null) {
            if (!node.isBoolean())
                throw new MalformedJasonSchemaException("uniqueItems should " +
                    "be a boolean");
            uniqueItems = node.getBooleanValue();
        }

        computeItems();
        computeAdditionalItems();
        finalCheck();
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final int nrItems = node.size();

        if (nrItems < minItems) {
            validationErrors.add("array has less than minItems elements");
            return false;
        }

        if (nrItems > maxItems) {
            validationErrors.add("array has more than maxItems elements");
            return false;
        }

        if (!additionalItemsOK && itemsTuples && nrItems > items.size()) {
            validationErrors.add("array has extra elements, "
                + "which the schema disallows");
            return false;
        }

        if (!uniqueItems)
            return true;

        try {
            CollectionUtils.toSet(node.getElements(), false);
        } catch (IllegalArgumentException e) {
            validationErrors.add("items in the array are not unique");
            return false;
        }

        return true;
    }

    private void computeItems()
        throws MalformedJasonSchemaException
    {
        final JsonNode node = schemaNode.get("items");

        if (node == null) {
            additionalItems = EMPTY_SCHEMA;
            return;
        }

        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (!node.isArray())
            throw new MalformedJasonSchemaException("items should be an " +
                "object or an array");

        itemsTuples = true;

        for (final JsonNode element: node) {
            if (!element.isObject())
                throw new MalformedJasonSchemaException("members of the items"
                    + " array should be objects");
            items.add(element);
        }

        if (items.isEmpty())
            throw new MalformedJasonSchemaException("the items array is empty");
    }

    private void computeAdditionalItems()
        throws MalformedJasonSchemaException
    {
        final JsonNode node = schemaNode.get("additionalItems");

        if (node == null)
            return;

        if (node.isBoolean()) {
            additionalItemsOK = node.getBooleanValue();
            return;
        }

        if (!node.isObject())
            throw new MalformedJasonSchemaException("additionalItems is "
                + "neither a boolean nor an object");

        additionalItems = node;
    }

    private void finalCheck()
        throws MalformedJasonSchemaException
    {
        if (!itemsTuples)
            return;

        final int len = items.size();

        if (minItems > len && !additionalItemsOK)
            throw new MalformedJasonSchemaException("minItems is greater "
                + "than what the schema allows (tuples, additional)");
        if (maxItems < len)
            throw new MalformedJasonSchemaException("maxItems is lower "
                + "than what the schema requires (tuples, additional)");

    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ArraySchemaProvider(items, additionalItems);
    }

}
