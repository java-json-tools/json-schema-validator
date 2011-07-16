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
import eel.kitchen.jsonschema.validators.ObjectSchemaProvider;
import eel.kitchen.jsonschema.validators.SchemaProvider;
import eel.kitchen.jsonschema.validators.misc.DependenciesValidator;
import eel.kitchen.jsonschema.validators.misc.RequiredValidator;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Validator for an object instance. It covers the following keywords:</p>
 * <ul>
 *     <li>properties (5.2);</li>
 *     <li>patternProperties (5.3);</li>
 *     <li>additionalProperties (5.4)</li>
 * </ul>
 */
public final class ObjectValidator
    extends AbstractValidator
{
    /**
     * Whether additional properties are allowed beyond the ones defined by
     * properties. It will only be set to false if the additionalProperties
     * keyword is set to false.
     */
    private boolean additionalPropertiesOK = true;

    /**
     * The additional properties schema. By default, an empty schema.
     */
    private JsonNode additionalProperties = EMPTY_SCHEMA;

    /**
     * Properties defined by the properties keyword, if any
     */
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();

    /**
     * Properties defined by the patternProperties keyword, if any
     */
    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    public ObjectValidator()
    {
        registerField("properties", NodeType.OBJECT);
        registerField("additionalProperties", NodeType.OBJECT);
        registerField("additionalProperties", NodeType.BOOLEAN);
        registerField("patternProperties", NodeType.OBJECT);

        registerValidator(new DependenciesValidator());
        registerValidator(new RequiredValidator());
    }

    /**
     * Validates the provided schema. It calls, in this order,
     * <code>computeProperties()</code>, <code>computeAdditional()</code>
     * and <code>computePatternProperties()</code> and returns the logical
     * and of these three.
     *
     * @return true if the schema is valid, false otherwise
     */
    @Override
    protected boolean doSetup()
    {
        return computeProperties() && computeAdditional()
            && computePatternProperties();
    }

    /**
     * Computes, and validates, the properties keywords. It will fail to
     * validate if one of the values defined in properties is not an object.
     *
     * @return false if the condition above is met
     */
    private boolean computeProperties()
    {
        final JsonNode node = schema.get("properties");

        if (node == null)
            return true;

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            value = entry.getValue();
            if (!value.isObject()) {
                schemaErrors.add("values of properties should be objects");
                return false;
            }
        }

        properties.putAll(map);
        return true;
    }

    /**
     * Computes the additionalPropertiesOK and additionalProperties keyword.
     *
     * @return always true
     */
    private boolean computeAdditional()
    {
        final JsonNode node = schema.path("additionalProperties");

        if (node.isBoolean())
            additionalPropertiesOK = node.getBooleanValue();

        if (node.isObject())
            additionalProperties = node;

        return true;
    }

    /**
     * Computes and validates the patternProperties keyword. It will fail to
     * validate if one of the fields is not a valid ECMA 262 regex,
     * or if the value for this field is not an object.
     *
     * @return false if either of the conditions above is met
     */
    private boolean computePatternProperties()
    {
        final JsonNode node = schema.get("patternProperties");

        if (node == null)
            return true;

        final Map<String, JsonNode> map = CollectionUtils.toMap(node.getFields());

        String regex;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            regex = entry.getKey();
            value = entry.getValue();
            if (!RhinoHelper.regexIsValid(regex)) {
                schemaErrors.add("invalid regex found in patternProperties");
                return false;
            }
            if (!value.isObject()) {
                schemaErrors.add("values of patternProperties should be objects");
                return false;
            }
            patternProperties.put(regex, value);
        }
        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final Set<String> fields = CollectionUtils.toSet(node.getFieldNames());
        final Collection<String> set = new HashSet<String>();

        fields.removeAll(properties.keySet());

        for (final String field: fields)
            for (final String regex: patternProperties.keySet())
                if (RhinoHelper.regMatch(regex, field)) {
                    set.add(field);
                    break;
                }

        fields.removeAll(set);

        if (additionalPropertiesOK || fields.isEmpty())
            return messages.isEmpty();

        messages.add("additional properties were found but schema forbids them");
        return false;
    }

    /**
     * Returns a schema provider for this object instance. It is,
     * with {@link ArrayValidator}, the only validator which returns a non
     * empty schema provider.
     *
     * @return an {@link ObjectSchemaProvider} built with the values computed
     * by this validator
     */
    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ObjectSchemaProvider(properties, patternProperties,
            additionalProperties);
    }
}
