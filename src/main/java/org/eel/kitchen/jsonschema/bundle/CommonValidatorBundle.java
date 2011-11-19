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

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.AdditionalPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DivisibleByKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.ExtendsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.FormatKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaxItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaxLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaximumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinimumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.PatternKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.RefKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.AdditionalItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.AdditionalPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DependenciesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DescriptionSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DisallowSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DivisibleBySyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DollarRefSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.DollarSchemaSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.EnumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMinimumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ExtendsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.FormatSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.IdSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.ItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MaxItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MaxLengthSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MinItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MinLengthSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.MinimumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.PatternPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.PatternSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.TitleSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.TypeSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.UniqueItemsSyntaxValidator;
import org.eel.kitchen.util.NodeType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.eel.kitchen.util.NodeType.*;

public abstract class CommonValidatorBundle
    implements ValidatorBundle
{
    protected final Map<String, SyntaxValidator> svMap
        = new HashMap<String, SyntaxValidator>();

    protected final Set<String> ignoredSV = new HashSet<String>();

    protected final Map<NodeType, Map<String, KeywordValidator>> kvMap
        = new EnumMap<NodeType, Map<String, KeywordValidator>>(NodeType.class);

    protected final Map<NodeType, Set<String>> ignoredKV
        = new EnumMap<NodeType, Set<String>>(NodeType.class);

    CommonValidatorBundle()
    {
        /*
         * Initialize keyword validator maps
         */
        for (final NodeType type: values()) {
            kvMap.put(type, new HashMap<String, KeywordValidator>());
            ignoredKV.put(type, new HashSet<String>());
        }

        /*
         * Register common keyword validators
         */
        registerKV("additionalItems", new AdditionalItemsKeywordValidator(),
            ARRAY);
        registerKV("additionalProperties",
            new AdditionalPropertiesKeywordValidator(), OBJECT);
        registerKV("dependencies", new DependenciesKeywordValidator(),
            values());
        registerKV("disallow", new DisallowKeywordValidator(), values());
        registerKV("divisibleBy", new DivisibleByKeywordValidator(), INTEGER,
            NUMBER);
        registerKV("enum", new EnumKeywordValidator(), values());
        registerKV("extends", new ExtendsKeywordValidator(), values());
        registerKV("format", new FormatKeywordValidator(), values());
        registerKV("maximum", new MaximumKeywordValidator(), INTEGER, NUMBER);
        registerKV("maxItems", new MaxItemsKeywordValidator(), ARRAY);
        registerKV("maxLength", new MaxLengthKeywordValidator(), STRING);
        registerKV("minimum", new MinimumKeywordValidator(), INTEGER, NUMBER);
        registerKV("minItems", new MinItemsKeywordValidator(), ARRAY);
        registerKV("minLength", new MinLengthKeywordValidator(), STRING);
        registerKV("pattern", new PatternKeywordValidator(), STRING);
        registerKV("type", new TypeKeywordValidator(), values());
        registerKV("uniqueItems", new UniqueItemsKeywordValidator(), ARRAY);
        registerKV("$ref", new RefKeywordValidator(), values());

        /*
         * Register ignored keyword validators
         */
        registerIgnoredKV("items", ARRAY);
        registerIgnoredKV("patternProperties", OBJECT);

        /*
         * Register common syntax validators
         */
        registerSV("additionalItems", new AdditionalItemsSyntaxValidator());
        registerSV("additionalProperties",
            new AdditionalPropertiesSyntaxValidator());
        registerSV("dependencies", new DependenciesSyntaxValidator());
        registerSV("description", new DescriptionSyntaxValidator());
        registerSV("disallow", new DisallowSyntaxValidator());
        registerSV("divisibleBy", new DivisibleBySyntaxValidator());
        registerSV("$ref", new DollarRefSyntaxValidator());
        registerSV("$schema", new DollarSchemaSyntaxValidator());
        registerSV("enum", new EnumSyntaxValidator());
        registerSV("exclusiveMaximum", new ExclusiveMaximumSyntaxValidator());
        registerSV("exclusiveMinimum", new ExclusiveMinimumSyntaxValidator());
        registerSV("extends", new ExtendsSyntaxValidator());
        registerSV("format", new FormatSyntaxValidator());
        registerSV("id", new IdSyntaxValidator());
        registerSV("items", new ItemsSyntaxValidator());
        registerSV("maximum", new MaximumSyntaxValidator());
        registerSV("maxItems", new MaxItemsSyntaxValidator());
        registerSV("maxLength", new MaxLengthSyntaxValidator());
        registerSV("minimum", new MinimumSyntaxValidator());
        registerSV("minItems", new MinItemsSyntaxValidator());
        registerSV("minLength", new MinLengthSyntaxValidator());
        registerSV("pattern", new PatternSyntaxValidator());
        registerSV("patternProperties", new PatternPropertiesSyntaxValidator());
        registerSV("title", new TitleSyntaxValidator());
        registerSV("type", new TypeSyntaxValidator());
        registerSV("uniqueItems", new UniqueItemsSyntaxValidator());

        registerIgnoredSV("default");

    }

    @Override
    public final Map<String, SyntaxValidator> syntaxValidators()
    {
        return Collections.unmodifiableMap(svMap);
    }

    @Override
    public final Set<String> ignoredSyntaxValidators()
    {
        return Collections.unmodifiableSet(ignoredSV);
    }

    @Override
    public Map<NodeType, Map<String, KeywordValidator>> keywordValidators()
    {
        return Collections.unmodifiableMap(kvMap);
    }

    @Override
    public Map<NodeType, Set<String>> ignoredKeywordValidators()
    {
        return Collections.unmodifiableMap(ignoredKV);
    }

    public final void registerSV(final String keyword,
        final SyntaxValidator sv)
    {
        svMap.put(keyword, sv);
    }

    public final void registerIgnoredSV(final String keyword)
    {
        ignoredSV.add(keyword);
    }

    public final void registerKV(final String keyword,
        final KeywordValidator kv, final NodeType... types)
    {
        for (final NodeType type: types)
            kvMap.get(type).put(keyword, kv);
    }

    public final void registerIgnoredKV(final String keyword,
        final NodeType... types)
    {
        for (final NodeType type: types)
            ignoredKV.get(type).add(keyword);
    }
}
