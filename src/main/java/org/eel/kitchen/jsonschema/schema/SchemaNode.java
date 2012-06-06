/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.RhinoHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SchemaNode
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private final SchemaContainer container;
    private final JsonNode node;

    private JsonNode additionalItems = EMPTY_SCHEMA;

    private final List<JsonNode> items = new ArrayList<JsonNode>();

    private JsonNode additionalProperties = EMPTY_SCHEMA;

    private final Map<String, JsonNode> properties
        = new HashMap<String, JsonNode>();

    private final Map<String, JsonNode> patternProperties
        = new HashMap<String, JsonNode>();

    public SchemaNode(final SchemaContainer container, final JsonNode node)
    {
        this.container = container;
        this.node = node;

        setupArraySchemas();
        setupObjectSchemas();
    }

    public SchemaContainer getContainer()
    {
        return container;
    }

    public JsonNode getNode()
    {
        return node;
    }

    public boolean isRef()
    {
        final JsonNode ref = node.path("$ref");
        if (!ref.isTextual())
            return false;

        try {
            new URI(ref.textValue());
            return true;
        } catch (URISyntaxException ignored) {
            return false;
        }
    }

    public JsonNode getArraySchema(final int index)
    {
        return index < items.size() ? items.get(index) : additionalItems;
    }

    public Set<JsonNode> getObjectSchemas(final String key)
    {
        final Set<JsonNode> ret = new HashSet<JsonNode>();

        if (properties.containsKey(key))
            ret.add(properties.get(key));

        for (final String regex: patternProperties.keySet())
            if (RhinoHelper.regMatch(regex, key))
                ret.add(patternProperties.get(regex));

        if (ret.isEmpty())
            ret.add(additionalProperties);

        return ret;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;

        final SchemaNode that = (SchemaNode) o;

        return container.equals(that.container)
            && node.equals(that.node);
    }

    @Override
    public int hashCode()
    {
        return 31 * container.hashCode() + node.hashCode();
    }

    private void setupArraySchemas()
    {
        JsonNode tmp;

        tmp = node.path("items");

        if (tmp.isObject()) {
            additionalItems = tmp;
            return;
        }

        if (tmp.isArray())
            for (final JsonNode item: tmp)
                items.add(item);

        tmp = node.path("additionalItems");

        if (tmp.isObject())
            additionalItems = tmp;
    }

    private void setupObjectSchemas()
    {
        JsonNode tmp;

        tmp = node.path("additionalProperties");

        if (tmp.isObject())
            additionalProperties = tmp;

        tmp = node.path("properties");

        if (tmp.isObject())
            properties.putAll(CollectionUtils.toMap(tmp.fields()));

        tmp = node.path("patternProperties");

        if (tmp.isObject())
            patternProperties.putAll(CollectionUtils.toMap(tmp.fields()));
    }
}
