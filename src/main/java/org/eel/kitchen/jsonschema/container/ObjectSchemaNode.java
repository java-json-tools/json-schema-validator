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
import org.codehaus.jackson.node.JsonNodeFactory;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.RhinoHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for object instance validation
 *
 * <p>This class is in charge of returning a set of schemas to validate a
 * child element of an object instance. For a given property name of the
 * instance, the rules are as follows:</p>
 * <ul>
 *     <li>if the name is exactly equal to a property name in {@code
 *     properties}, the corresponding schema is added to the set;
 *     </li>
 *     <li>if the name matches one or more patterns defined in {@code
 *     patternProperties}, then the corresponding schemas are added to the set;
 *     </li>
 *     <li>if, at this point, the set is empty, the content of {@code
 *     additionalProperties} is added to the set (or an empty schema if {@code
 *     additionalProperties} is undefined), and the set is returned.
 *     </li>
 * </ul>
 *
 */
public final class ObjectSchemaNode
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    /**
     * The contents of {@code properties}
     */
    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();

    /**
     * The contents of {@code patternProperties}
     */
    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    /**
     * The contents of {@code additionalProperties}
     */
    private JsonNode additionalProperties = EMPTY_SCHEMA;

    public ObjectSchemaNode(final JsonNode schema)
    {
        setupObjectNodes(schema);
    }

    private void setupObjectNodes(final JsonNode schema)
    {
        JsonNode node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.getFields()));

        node = schema.path("additionalProperties");

        if (node.isObject())
            additionalProperties = node;
    }

    public Collection<JsonNode> objectPath(final String path)
    {
        final Set<JsonNode> ret = new HashSet<JsonNode>();

        if (properties.containsKey(path))
            ret.add(properties.get(path));

        for (final Map.Entry<String, JsonNode> entry:
            patternProperties.entrySet())
            if (RhinoHelper.regMatch(entry.getKey(), path))
                ret.add(entry.getValue());

        if (ret.isEmpty())
            ret.add(additionalProperties);

        return ret;
    }
}
