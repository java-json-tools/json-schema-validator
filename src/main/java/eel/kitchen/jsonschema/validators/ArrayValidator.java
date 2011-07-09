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

package eel.kitchen.jsonschema.validators;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArrayValidator
    extends AbstractValidator
{
    private int minItems = 0, maxItems = Integer.MAX_VALUE;
    private boolean uniqueItems = false;
    private final List<JsonNode> items = new ArrayList<JsonNode>();

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

        node = schemaNode.get("items");

        if (node == null) {
            items.add(EMPTY_SCHEMA);
            return;
        }

        if (node.isObject()) {
            items.add(node);
            return;
        }

        if (!node.isArray())
            throw new MalformedJasonSchemaException("items should be an " +
                "object or an array");

        try {
            items.addAll(CollectionUtils.toSet(node.getElements(), false));
        } catch (Exception e) {
            throw new MalformedJasonSchemaException("duplicate members in the" +
                " items array");
        }

        if (items.isEmpty())
            throw new MalformedJasonSchemaException("the items array is empty");

        for (final JsonNode element: items)
            if (!element.isObject())
                throw new MalformedJasonSchemaException("members of the items" +
                    " array should be objects");
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

    @Override
    public List<JsonNode> getSchemasForPath(final String subPath)
    {
        return Collections.unmodifiableList(items);
    }
}
