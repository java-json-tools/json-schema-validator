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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validator for the {@code dependencies} keyword
 *
 * <p>This validator covers both property dependencies and schema
 * dependencies.</p>
 */
public final class DependenciesKeywordValidator
    extends KeywordValidator
{
    /**
     * Map of simple dependencies (ie, property dependencies)
     */
    private final Map<String, Set<String>> simple
        = new HashMap<String, Set<String>>();

    /**
     * Map of schema dependencies
     */
    private final Map<String, JsonNode> schemas
        = new HashMap<String, JsonNode>();

    public DependenciesKeywordValidator(final JsonNode schema)
    {
        super(NodeType.OBJECT);
        final Map<String, JsonNode> fields
            = CollectionUtils.toMap(schema.get("dependencies").fields());

        String key;
        JsonNode value;

        /*
         * Walk through the list of fields:
         *
         * - if we encounter an object, this is a schema dependency;
         * - otherwise this is a simple dependency.
         *
         * Remember that we went through syntax validation first,
         * so we are guaranteed about the correctness of the schema.
         */
        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value.isObject())
                schemas.put(key, value);
            else
                simple.put(key, simpleDepdency(value));
        }
    }

    /**
     * Compute a simple dependency
     *
     * @param value the value of the object's member
     * @return a set of property names
     */
    private Set<String> simpleDepdency(final JsonNode value)
    {
        final Set<String> ret = new HashSet<String>();

        /*
         * This works: for non container values, an empty iterator is
         * returned. And we can only be called from here if the dependencies
         * syntax is correct, so it's either an array...
         */
        for (final JsonNode tmp: value)
            ret.add(tmp.textValue());

        /*
         * Or a string value.
         */
        if (ret.isEmpty())
            ret.add(value.textValue());

        return ret;
    }


    @Override
    public void validate(final ValidationContext context,
        final JsonNode instance)
    {
        /*
         * Grab the set of property names from the instance
         */
        final Set<String> fields = CollectionUtils.toSet(instance.fieldNames());

        /*
         * Simple dependencies: make a copy of the simple dependency map,
         * and only retain what's actually in the instance
         */
        final Map<String, Set<String>> simpleDeps
            = new HashMap<String, Set<String>>(simple);

        simpleDeps.keySet().retainAll(fields);

        /*
         * We don't bother about determining single property dependencies,
         * we just swallow all found simple dependencies in a single set...
         */
        final Set<String> fullSet = new HashSet<String>();

        for (final Set<String> set: simpleDeps.values())
            fullSet.addAll(set);

        /*
         * ... And check that the instance contains them all.
         */
        if (!fields.containsAll(fullSet))
            context.addMessage("missing property dependencies");

        /*
         * Schema dependencies: the principle is the same,
         * make a copy of the schemas map and only retain whatever properties
         * are present in the instance.
         */
        final Map<String, JsonNode> schemaDeps
            = new HashMap<String, JsonNode>(schemas);

        schemaDeps.keySet().retainAll(fields);

        /*
         * In this case however, we need to generate other schemas,
         * so we have to grab the schema factory and current context (schema
         * container) in order to generate new schemas
         */
        final SchemaContainer container = context.getContainer();
        final JsonSchemaFactory factory = context.getFactory();
        JsonSchema subSchema;

        for (final JsonNode node: schemaDeps.values()) {
            subSchema = factory.create(container, node);
            subSchema.validate(context, instance);
        }
    }
}
