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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.ArrayChildrenSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DependenciesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DivisibleBySyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.EnumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMaximumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMinimumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PatternPropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PatternSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PositiveIntegerSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.TypeKeywordSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.URISyntaxChecker;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Schema syntax validator
 *
 * <p>Note that schemas are not validated "in depth", only the first level of
 * keywords will be validated. This is by design (for now) since recursing
 * through schemas is not as simple as it sounds, we cannot just blindly
 * recurse through object instances (consider {@code enum}).</p>
 */
// FIXME: try and recurse through schemas, it can be done with a little work,
// but it has to be triggered from _within_ validators. Ouch.
public final class SyntaxValidator
{
    //FIXME: make this a "LRUSet"
    private static final Set<JsonNode> done = new HashSet<JsonNode>();

    private static final Map<String, EnumSet<NodeType>> typeChecks
        = new HashMap<String, EnumSet<NodeType>>();

    private static final Map<String, SyntaxChecker> syntaxChecks
        = new HashMap<String, SyntaxChecker>();

    static {
        addKeyword("additionalItems", NodeType.BOOLEAN, NodeType.OBJECT);

        addKeyword("additionalProperties", NodeType.BOOLEAN, NodeType.OBJECT);

        addKeyword("dependencies", NodeType.OBJECT);
        syntaxChecks.put("dependencies",
            DependenciesSyntaxChecker.getInstance());

        addKeyword("description", NodeType.STRING);

        addKeyword("disallow", NodeType.STRING, NodeType.ARRAY);
        syntaxChecks.put("disallow", new TypeKeywordSyntaxChecker("disallow"));

        addKeyword("divisibleBy", NodeType.INTEGER, NodeType.NUMBER);
        syntaxChecks.put("divisibleBy", DivisibleBySyntaxChecker.getInstance());

        addKeyword("enum", NodeType.ARRAY);
        syntaxChecks.put("enum", EnumSyntaxChecker.getInstance());

        addKeyword("exclusiveMinimum", NodeType.BOOLEAN);
        syntaxChecks.put("exclusiveMinimum",
            ExclusiveMinimumSyntaxChecker.getInstance());

        addKeyword("exclusiveMaximum", NodeType.BOOLEAN);
        syntaxChecks.put("exclusiveMaximum",
            ExclusiveMaximumSyntaxChecker.getInstance());

        addKeyword("extends", NodeType.OBJECT, NodeType.ARRAY);
        syntaxChecks.put("extends", new ArrayChildrenSyntaxChecker("extends",
            NodeType.OBJECT));

        addKeyword("format", NodeType.STRING);

        addKeyword("id", NodeType.STRING);
        syntaxChecks.put("id", new URISyntaxChecker("id"));

        addKeyword("items", NodeType.OBJECT, NodeType.ARRAY);
        syntaxChecks.put("items", new ArrayChildrenSyntaxChecker("items",
            NodeType.OBJECT));

        addKeyword("maximum", NodeType.INTEGER, NodeType.NUMBER);

        addKeyword("maxItems", NodeType.INTEGER);
        syntaxChecks.put("maxItems",
            new PositiveIntegerSyntaxChecker("maxItems"));

        addKeyword("maxLength", NodeType.INTEGER);
        syntaxChecks.put("maxLength",
            new PositiveIntegerSyntaxChecker("maxLength"));

        addKeyword("minimum", NodeType.INTEGER, NodeType.NUMBER);

        addKeyword("minItems", NodeType.INTEGER);
        syntaxChecks.put("minItems",
            new PositiveIntegerSyntaxChecker("minItems"));

        addKeyword("minLength", NodeType.INTEGER);
        syntaxChecks.put("minLength",
            new PositiveIntegerSyntaxChecker("minLength"));

        addKeyword("pattern", NodeType.STRING);
        syntaxChecks.put("pattern", PatternSyntaxChecker.getInstance());

        addKeyword("patternProperties", NodeType.OBJECT);
        syntaxChecks.put("patternProperties",
            PatternPropertiesSyntaxChecker.getInstance());

        addKeyword("properties", NodeType.OBJECT);
        syntaxChecks.put("properties", PropertiesSyntaxChecker.getInstance());

        addKeyword("required", NodeType.BOOLEAN);

        addKeyword("title", NodeType.STRING);

        addKeyword("type", NodeType.STRING, NodeType.ARRAY);
        syntaxChecks.put("type", new TypeKeywordSyntaxChecker("type"));

        addKeyword("uniqueItems", NodeType.BOOLEAN);

        addKeyword("$ref", NodeType.STRING);
        syntaxChecks.put("$ref", new URISyntaxChecker("$ref"));

        addKeyword("$schema", NodeType.STRING);
        syntaxChecks.put("$schema", new URISyntaxChecker("$schema"));
    }

    private static void addKeyword(final String keyword,
        final NodeType... types)
    {
        final EnumSet<NodeType> set = EnumSet.noneOf(NodeType.class);

        set.addAll(Arrays.asList(types));

        typeChecks.put(keyword, set);
    }

    public static synchronized void validate(final ValidationReport report,
        final JsonNode schema)
    {
        if (done.contains(schema))
            return;

        final Map<String, JsonNode> fields
            = CollectionUtils.toMap(schema.fields());

        String fieldName;
        JsonNode child;
        EnumSet<NodeType> types;
        SyntaxChecker checker;

        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            fieldName = entry.getKey();
            child = entry.getValue();
            types = typeChecks.get(fieldName);
            checker = syntaxChecks.get(fieldName);
            if (types != null && !types.contains(NodeType.getNodeType(child))) {
                report.addMessage(fieldName + " is of wrong type");
                continue;
            }
            if (checker != null)
                checker.checkValue(report, schema);
        }

        if (report.isSuccess())
            done.add(schema);
    }
}
