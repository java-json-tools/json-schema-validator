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
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.JsonPointer;
import org.eel.kitchen.util.RhinoHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ValidJsonSchema
    extends JsonSchema
{
    private static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    private static final KeywordFactory factory = new KeywordFactory();

    private final Set<KeywordValidator> validators;

    /**
     * The contents of {@code items}
     */
    private final List<JsonNode> items = new ArrayList<JsonNode>();

    /**
     * The contents of {@code additionalItems}
     */
    private JsonNode additionalItems = EMPTY_SCHEMA;

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

    private final JsonNode parent;

    ValidJsonSchema(final JsonNode parent, final JsonNode schema)
    {
        this.parent = parent;
        validators = factory.getValidators(schema);
        setupArrayNodes(schema);
        setupObjectNodes(schema);
    }

    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        final JsonNode oldParent = report.getSchema();

        report.setSchema(parent);

        for (final KeywordValidator validator: validators)
            validator.validateInstance(report, instance);

        if (!(instance.isContainerNode() || report.isSuccess())) {
            report.setSchema(oldParent);
            return;
        }

        final JsonPointer ptr = report.getPath();

        JsonPointer current;

        if (instance.isArray()) {
            JsonNode subSchema;
            int i = 0;
            for (final JsonNode element: instance) {
                current = ptr.append(i);
                report.setPath(current);
                subSchema = arrayPath(i);
                JsonSchema.fromNode(parent, subSchema)
                    .validate(report, element);
                i++;
            }
            report.setSchema(oldParent);
            report.setPath(ptr);
            return;
        }

        // Can only be an object now
        final Map<String, JsonNode> map
            = CollectionUtils.toMap(instance.fields());

        String key;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            current = ptr.append(key);
            report.setPath(current);
            for (final JsonNode subSchema: objectPath(key))
                JsonSchema.fromNode(parent, subSchema).validate(report, value);
        }

        report.setSchema(oldParent);
        report.setPath(ptr);
    }

    private void setupArrayNodes(final JsonNode schema)
    {
        JsonNode node = schema.path("items");

        /**
         * We don't bother at this point: if items is a schema,
         * then it will be used for each and every element of the instance to
         * validate -- it's just as if additionalItems were never defined.
         * So, as items is defined as a list above, we just leave it empty
         * and assign the contents of the keyword to additionalItems.
         */
        if (node.isObject()) {
            additionalItems = node;
            return;
        }

        if (node.isArray())
            for (final JsonNode item: node)
                items.add(item);

        node = schema.path("additionalItems");

        if (node.isObject())
            additionalItems = node;
    }

    private void setupObjectNodes(final JsonNode schema)
    {
        JsonNode node = schema.path("properties");

        if (node.isObject())
            properties.putAll(CollectionUtils.toMap(node.fields()));

        node = schema.path("patternProperties");

        if (node.isObject())
            patternProperties.putAll(CollectionUtils.toMap(node.fields()));

        node = schema.path("additionalProperties");

        if (node.isObject())
            additionalProperties = node;
    }

    private Collection<JsonNode> objectPath(final String path)
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

    private JsonNode arrayPath(final int index)
    {
        return index < items.size() ? items.get(index) : additionalItems;
    }
}
