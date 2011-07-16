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

public final class ObjectValidator
    extends AbstractValidator
{
    private boolean additionalPropertiesOK = true;
    private JsonNode additionalProperties = EMPTY_SCHEMA;
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();
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

    @Override
    protected boolean doSetup()
    {
        return computeProperties() && computeAdditional()
            && computePatternProperties();
    }

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
                messages.add("values of properties should be objects");
                return false;
            }
        }

        properties.putAll(map);
        return true;
    }

    private boolean computeAdditional()
    {
        final JsonNode node = schema.path("additionalProperties");

        if (node.isBoolean())
            additionalPropertiesOK = node.getBooleanValue();

        if (node.isObject())
            additionalProperties = node;

        return true;
    }

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
                messages.add("invalid regex found in patternProperties");
                return false;
            }
            if (!value.isObject()) {
                messages.add("values of patternProperties should be objects");
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

    @Override
    public SchemaProvider getSchemaProvider()
    {
        return new ObjectSchemaProvider(properties, patternProperties,
            additionalProperties);
    }
}
