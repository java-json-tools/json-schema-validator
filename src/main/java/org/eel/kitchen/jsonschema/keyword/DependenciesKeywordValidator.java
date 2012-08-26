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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.RefResolverJsonValidator;

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
    private static final Joiner DEP_JOINER = Joiner.on("; ").skipNulls();
    /**
     * Map of simple dependencies (ie, property dependencies)
     */
    private final SetMultimap<String, String> simple;

    /**
     * Map of schema dependencies
     */
    private final Map<String, JsonNode> schemas;

    public DependenciesKeywordValidator(final JsonNode schema)
    {
        super("dependencies", NodeType.OBJECT);
        final Map<String, JsonNode> fields
            = JacksonUtils.nodeToMap(schema.get("dependencies"));

        final ImmutableMap.Builder<String, JsonNode> schemaBuilder
            = new ImmutableMap.Builder<String, JsonNode>();

        final ImmutableSetMultimap.Builder<String, String> simpleBuilder
            = new ImmutableSetMultimap.Builder<String, String>();

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
            if (value.isObject()) { // schema dependency
                schemaBuilder.put(key, value);
                continue;
            }
            if (value.size() == 0) // single property dependency
                simpleBuilder.put(key, value.textValue());
            else // multiple property dependency
                for (final JsonNode element: value)
                    simpleBuilder.put(key, element.textValue());
        }

        schemas = schemaBuilder.build();
        simple = simpleBuilder.build();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        /*
         * Grab the set of property names from the instance
         */
        final Set<String> fields = JacksonUtils.fieldNames(instance);

        /*
         * Simple dependencies: calculate the needed fields according to
         * available instance fields. SetMultimap's .get() method returns an
         * empty collection if a key does not exist,
         * which allows the following code.
         */
        final Set<String> neededFields = new HashSet<String>();

        for (final String field: fields)
            neededFields.addAll(simple.get(field));

        if (!fields.containsAll(neededFields))
            report.addMessage("missing property dependencies");

        /*
         * Schema dependencies: make a copy of the schemas map and only retain
         * whatever properties are present in the instance.
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

        JsonValidator validator;

        for (final JsonNode subSchema: schemaDeps.values()) {
            validator = new RefResolverJsonValidator(factory, subSchema);
            while (validator.validate(context, report, instance))
                validator = validator.next();
            context.setContainer(container);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(keyword).append(": ");

        if (simple.isEmpty() && schemas.isEmpty())
            return sb.append("none??").toString();

        DEP_JOINER.appendTo(sb, simple.isEmpty() ? null : simple,
            schemasToString());

        return sb.toString();
    }

    private String schemasToString()
    {
        if (schemas.isEmpty())
            return null;

        return "further schema validations for properties " + schemas.keySet();
    }
}
