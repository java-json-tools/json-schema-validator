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

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.AdditionalPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.DivisibleByKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.ExtendsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.FormatKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaxItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaxLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MaximumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.MinimumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.PatternKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.DependenciesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.DivisibleBySyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.EnumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMaximumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExclusiveMinimumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ExtendsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.ItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PatternPropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PatternSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PositiveIntegerSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SimpleSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.TypeKeywordSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.URISyntaxChecker;
import org.eel.kitchen.jsonschema.util.NodeType;

/**
 * Utility class to distribute default keyword bundles.
 *
 * <p>Right now, it only distributes a draft v3 keyword bundle, via the
 * {@link #defaultBundle()} method.</p>
 */
public final class KeywordBundles
{
    private static final KeywordBundle DEFAULT_BUNDLE;

    private KeywordBundles()
    {
    }

    static {
        Keyword keyword;
        SyntaxChecker checker;

        DEFAULT_BUNDLE = new KeywordBundle();

        checker = new SimpleSyntaxChecker("additionalItems", NodeType.BOOLEAN,
            NodeType.OBJECT);
        keyword = Keyword.withName("additionalItems").withSyntaxChecker(checker)
            .withValidatorClass(AdditionalItemsKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("additionalProperties",
            NodeType.BOOLEAN, NodeType.OBJECT);
        keyword = Keyword.withName("additionalProperties")
            .withSyntaxChecker(checker)
            .withValidatorClass(AdditionalPropertiesKeywordValidator.class)
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("dependencies")
            .withSyntaxChecker(DependenciesSyntaxChecker.getInstance())
            .withValidatorClass(DependenciesKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("description", NodeType.STRING);
        keyword = Keyword.withName("description").withSyntaxChecker(checker)
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new TypeKeywordSyntaxChecker("disallow");
        keyword = Keyword.withName("disallow").withSyntaxChecker(checker)
            .withValidatorClass(DisallowKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("divisibleBy")
            .withSyntaxChecker(DivisibleBySyntaxChecker.getInstance())
            .withValidatorClass(DivisibleByKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("enum")
            .withSyntaxChecker(EnumSyntaxChecker.getInstance())
            .withValidatorClass(EnumKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("exclusiveMinimum")
            .withSyntaxChecker(ExclusiveMinimumSyntaxChecker.getInstance())
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("exclusiveMaximum")
            .withSyntaxChecker(ExclusiveMaximumSyntaxChecker.getInstance())
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("extends")
            .withSyntaxChecker(ExtendsSyntaxChecker.getInstance())
            .withValidatorClass(ExtendsKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("format", NodeType.STRING);
        keyword = Keyword.withName("format").withSyntaxChecker(checker)
            .withValidatorClass(FormatKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new URISyntaxChecker("id");
        keyword = Keyword.withName("id").withSyntaxChecker(checker).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("items")
            .withSyntaxChecker(ItemsSyntaxChecker.getInstance()).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("maximum", NodeType.INTEGER,
            NodeType.NUMBER);
        keyword = Keyword.withName("maximum").withSyntaxChecker(checker)
            .withValidatorClass(MaximumKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new PositiveIntegerSyntaxChecker("maxItems");
        keyword = Keyword.withName("maxItems").withSyntaxChecker(checker)
            .withValidatorClass(MaxItemsKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new PositiveIntegerSyntaxChecker("maxLength");
        keyword = Keyword.withName("maxLength").withSyntaxChecker(checker)
            .withValidatorClass(MaxLengthKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("minimum", NodeType.INTEGER,
            NodeType.NUMBER);
        keyword = Keyword.withName("minimum").withSyntaxChecker(checker)
            .withValidatorClass(MinimumKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new PositiveIntegerSyntaxChecker("minItems");
        keyword = Keyword.withName("minItems").withSyntaxChecker(checker)
            .withValidatorClass(MinItemsKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new PositiveIntegerSyntaxChecker("minLength");
        keyword = Keyword.withName("minLength").withSyntaxChecker(checker)
            .withValidatorClass(MinLengthKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("pattern")
            .withSyntaxChecker(PatternSyntaxChecker.getInstance())
            .withValidatorClass(PatternKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("patternProperties")
            .withSyntaxChecker(PatternPropertiesSyntaxChecker.getInstance())
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        keyword = Keyword.withName("properties")
            .withSyntaxChecker(PropertiesSyntaxChecker.getInstance())
            .withValidatorClass(PropertiesKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("required", NodeType.BOOLEAN);
        keyword = Keyword.withName("required").withSyntaxChecker(checker)
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("title", NodeType.STRING);
        keyword = Keyword.withName("title").withSyntaxChecker(checker).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new TypeKeywordSyntaxChecker("type");
        keyword = Keyword.withName("type").withSyntaxChecker(checker)
            .withValidatorClass(TypeKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new SimpleSyntaxChecker("uniqueItems", NodeType.BOOLEAN);
        keyword = Keyword.withName("uniqueItems").withSyntaxChecker(checker)
            .withValidatorClass(UniqueItemsKeywordValidator.class).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new URISyntaxChecker("$ref");
        keyword = Keyword.withName("$ref").withSyntaxChecker(checker).build();
        DEFAULT_BUNDLE.registerKeyword(keyword);

        checker = new URISyntaxChecker("$schema");
        keyword = Keyword.withName("$schema").withSyntaxChecker(checker)
            .build();
        DEFAULT_BUNDLE.registerKeyword(keyword);
    }

    /**
     * The standard draft v3 keyword bundle
     *
     * @return a {@link KeywordBundle}
     */
    public static KeywordBundle defaultBundle()
    {
        return DEFAULT_BUNDLE.copy();
    }
}
