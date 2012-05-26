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
import org.eel.kitchen.jsonschema.syntax.AdditionalItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.AdditionalPropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DependenciesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DescriptionSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DisallowSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DivisibleBySyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DollarRefSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DollarSchemaSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.EnumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMaximumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMinimumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExtendsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.FormatSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.IdSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.MaxItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.MaxLengthSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.MaximumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.MinItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.MinLengthSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.MinimumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PatternPropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PatternSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.RequiredSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.TitleSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.TypeSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.UniqueItemsSyntaxChecker;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

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

    private static final Map<String, EnumSet<NodeType>> TYPE_CHECKS
        = new HashMap<String, EnumSet<NodeType>>();

    private static final Map<String, SyntaxChecker> SYNTAX_CHECKS
        = new HashMap<String, SyntaxChecker>();

    static {
        SYNTAX_CHECKS.put("additionalItems",
            AdditionalItemsSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("additionalProperties",
            AdditionalPropertiesSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("dependencies",
            DependenciesSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("description",
            DescriptionSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("disallow", DisallowSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("divisibleBy",
            DivisibleBySyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("enum", EnumSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("exclusiveMinimum",
            ExclusiveMinimumSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("exclusiveMaximum",
            ExclusiveMaximumSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("extends", ExtendsSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("format", FormatSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("id", IdSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("items", ItemsSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("maximum", MaximumSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("maxItems", MaxItemsSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("maxLength", MaxLengthSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("minimum", MinimumSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("minItems", MinItemsSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("minLength", MinLengthSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("pattern", PatternSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("patternProperties",
            PatternPropertiesSyntaxChecker.getInstance());

        addKeyword("properties", NodeType.OBJECT);
        SYNTAX_CHECKS.put("properties", PropertiesSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("required", RequiredSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("title", TitleSyntaxChecker.getInstance());

        addKeyword("type", NodeType.STRING, NodeType.ARRAY);
        SYNTAX_CHECKS.put("type", TypeSyntaxChecker.getInstance());

        SYNTAX_CHECKS.put("uniqueItems", UniqueItemsSyntaxChecker.getInstance
            ());

        addKeyword("$ref", NodeType.STRING);
        SYNTAX_CHECKS.put("$ref", DollarRefSyntaxChecker.getInstance());

        addKeyword("$schema", NodeType.STRING);
        SYNTAX_CHECKS.put("$schema", DollarSchemaSyntaxChecker.getInstance());
    }

    private static void addKeyword(final String keyword, final NodeType type,
        final NodeType... types)
    {
        TYPE_CHECKS.put(keyword, EnumSet.of(type, types));
    }

    public void validate(final ValidationReport report,
        final JsonNode schema)
    {
        synchronized (done) {
            if (done.contains(schema))
                return;

            final Map<String, JsonNode> fields = CollectionUtils
                .toMap(schema.fields());

            String fieldName;
            JsonNode node;
            EnumSet<NodeType> types;
            SyntaxChecker checker;
            NodeType nodeType;

            for (final Map.Entry<String, JsonNode> entry : fields.entrySet()) {
                fieldName = entry.getKey();
                node = entry.getValue();
                types = TYPE_CHECKS.get(fieldName);
                nodeType = NodeType.getNodeType(node);
                if (types != null && !types.contains(nodeType)) {
                    report.addMessage(fieldName + " is of wrong type");
                    continue;
                }
                checker = SYNTAX_CHECKS.get(fieldName);
                if (checker != null)
                    checker.checkSyntax(report, schema);
            }

            if (report.isSuccess())
                done.add(schema);
        }
    }
}
