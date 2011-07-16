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
import eel.kitchen.jsonschema.validators.misc.EnumValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Validator for an array instance. This implements validation for the
 * following keywords (the corresponding section in the draft is mentioned in
 * parentheses):</p>
 * <ul>
 *     <li>items (5.5);</li>
 *     <li>additionalItems (5.6);</li>
 *     <li>minItems and maxItems (5.13 and 5.14);</li>
 *     <li>uniqueItems (5.15).</li>
 * </ul>
 * <p>This is also the only validator with {@link ObjectValidator} which
 * returns a non empty {@link SchemaProvider}.</p>
 */
public final class ArrayValidator
    extends AbstractValidator
{
    /**
     * minItems and maxItems. By default, they are set to 0 and
     * <code>Integer.MAX_VALUE</code> respectively, so as to always succeed,
     * except when an overflow occurs - see <code>doSetup()</code>
     */
    private int minItems = 0, maxItems = Integer.MAX_VALUE;

    /**
     * Whether the items in the whole array should be unique
     */
    private boolean uniqueItems = false;

    /**
     * Values in the items array, if tuple item validation is in use.
     * If the items keyword is an object, its value will be put into
     * <code>additionalItems</code> instead
     */
    private final List<JsonNode> items = new LinkedList<JsonNode>();

    /**
     * Whether tuple validation is in effect
     */
    private boolean itemsTuples = false;

    /**
     * Are further items allowed? This will be true except if the
     * additionalItems keyword is set to false
     */
    private boolean additionalItemsOK = true;

    /**
     * Value of the additionalItems keyword if it is not a boolean,
     * or of items if tuple validation is not in effect
     */
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

        registerValidator(new EnumValidator());
    }


    /**
     * <p>Validates the schema and fills in the different parameters. In itself,
     * this function only validates minItems, maxItems and uniqueItems. The
     * other schema validation steps are provided by
     * <code>computeItems()</code>, <code>computeAdditionalItems()</code> and
     * <code>finalCheck()</code>.</p>
     *
     * <p>At this step, the schema validation stops and this function returns
     * false if one of the following conditions is met:</p>
     * <ul>
     *     <li>minItems or maxItems overflow (they do not fit in an integer);
     *     </li>
     *     <li>minItems or maxItems are lower than 0;</li>
     *     <li>minItems is greater than maxItems.</li>
     * </ul>
     *
     * @return true if the schema is deemed valid by this function and its
     * three helpers
     */
    @Override
    protected boolean doSetup()
    {
        JsonNode node;

        node = schema.get("minItems");
        if (node != null) {
            if (!node.isInt()) {
                schemaErrors.add("minItems overflow");
                return false;
            }
            minItems = node.getIntValue();
            if (minItems < 0) {
                schemaErrors.add("minItems is negative");
                return false;
            }
        }

        node = schema.get("maxItems");
        if (node != null) {
            if (!node.isInt()) {
                schemaErrors.add("maxItems overflow");
                return false;
            }
            maxItems = node.getIntValue();
            if (maxItems < 0) {
                schemaErrors.add("maxItems is negative");
                return false;
            }
        }

        if (maxItems < minItems) {
            schemaErrors.add("minItems is greater than maxItems");
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

    /**
     * <p>Checks for the validity of the items keyword. The only real check
     * is if items is an array: in this case it is required that all elements
     * in the array be schemas, therefore JSON objects.</p>
     *
     * @return false if items is an array and one of its elements is not a
     * JSON object
     */
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
                schemaErrors.add("members of the items array should be objects");
                return false;
            }
            items.add(element);
        }

        return true;
    }

    /**
     * Sets up the additionalItems or additionalItemsOK,
     * depending on the value of the additionalItems field. Triggers a schema
     * validation failure if items tuple validation is not in effect and
     * additionalItems is an object.
     *
     * @return false under the conditions mentioned above, otherwise true
     */
    private boolean computeAdditionalItems()
    {
        final JsonNode node = schema.get("additionalItems");

        if (node == null)
            return true;

        if (node.isBoolean()) {
            additionalItemsOK = node.getBooleanValue();
            return true;
        }

        if (!itemsTuples) {
            schemaErrors.add("additionalItems is an object but tuple "
                + "validation is not in effect");
            return false;
        }

        additionalItems = node;
        return true;
    }

    /**
     * Final checking. Trigger a schema validation failure if minItems is
     * strictly greater than the number of schemas in items in case of a
     * tuple validation, and additionalItems is set to false.
     *
     * @return false if the above condition is met, otherwise true
     */
    private boolean finalCheck()
    {
        if (!itemsTuples)
            return true;

        if (additionalItemsOK)
            return true;

        if (minItems <= items.size())
            return true;

        schemaErrors.add("minItems is greater than what the schema allows "
            + "(tuples, additional)");
        return false;
    }

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ArraySchemaProvider(items, additionalItems);
    }

}
