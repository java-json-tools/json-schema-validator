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

import eel.kitchen.jsonschema.validators.type.ObjectValidator;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Schema provider spawned by {@link ObjectValidator}.
 *
 * @see {@link SchemaProvider}
 * @see {@link ObjectValidator}
 */
public final class ObjectSchemaProvider
    implements SchemaProvider
{

    /**
     * Map of properties and additional properties found in the schema,
     * if any, and of patternProperties, if any
     */
    private final Map<String, JsonNode>
        properties = new HashMap<String, JsonNode>(),
        patternProperties = new HashMap<String, JsonNode>();

    /**
     * Schema for additional properties
     */
    private final JsonNode additionalProperties;

    public ObjectSchemaProvider(final Map<String, JsonNode> properties,
        final Map<String, JsonNode> patternProperties,
        final JsonNode additionalProperties)
    {
        this.properties.putAll(properties);
        this.patternProperties.putAll(patternProperties);
        this.additionalProperties = additionalProperties;
    }

    /**
     * <p>Get the schema associated with the relative path in the current
     * instance. In order, it will try to find and then return:</p>
     * <ul>
     *     <li>the corresponding schema in the properties map on an exact key
     *     match;</li>
     *     <li>the corresponding schema to a regex in patternProperties if
     *     the path matches that regex;</li>
     *     <li>the schema defined by additionalProperties</li>
     * </ul>
     *
     * @param path the subpath
     * @return the corresponding schema
     */
    @Override
    public JsonNode getSchemaForPath(final String path)
    {
        if (properties.containsKey(path))
            return properties.get(path);

        for (final String regex: patternProperties.keySet())
            if (RhinoHelper.regMatch(regex, path))
                return patternProperties.get(regex);

        return additionalProperties;
    }
}
