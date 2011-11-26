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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.common
    .AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common
    .AdditionalPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.DivisibleByKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.ExtendsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.FormatKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaxItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaxLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaximumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinimumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.PatternKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.RefKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.syntax.common.AdditionalItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common
    .AdditionalPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DependenciesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DescriptionSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DisallowSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DivisibleBySyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DollarSchemaSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.EnumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common
    .ExclusiveMaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common
    .ExclusiveMinimumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.ExtendsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.FormatSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.IdSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.ItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.MaxItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.MaxLengthSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.MaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.MinItemsSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.MinLengthSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.MinimumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common
    .PatternPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.PatternSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.TitleSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.TypeSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.UniqueItemsSyntaxValidator;

import static org.eel.kitchen.util.NodeType.*;

/**
 * The common set of validators used by existing JSON Schema specifications
 */
public abstract class CommonValidatorBundle
    extends ValidatorBundle
{
    protected CommonValidatorBundle()
    {

        /* additionalItems */
        registerSV("additionalItems",
            AdditionalItemsSyntaxValidator.getInstance());
        registerKV("additionalItems",
            AdditionalItemsKeywordValidator.getInstance(), ARRAY);

        /* additionalProperties */
        registerSV("additionalProperties",
            AdditionalPropertiesSyntaxValidator.getInstance());
        registerKV("additionalProperties",
            AdditionalPropertiesKeywordValidator.getInstance(), OBJECT);

        /* default */
        registerIgnoredSV("default");
        registerIgnoredKV("default", values());

        /* dependencies */
        registerSV("dependencies",
            DependenciesSyntaxValidator.getInstance());
        registerKV("dependencies", DependenciesKeywordValidator.getInstance(),
            values());

        /* description */
        registerSV("description", DescriptionSyntaxValidator.getInstance());
        registerIgnoredKV("description", values());

        /* disallow */
        registerSV("disallow", DisallowSyntaxValidator.getInstance());
        registerKV("disallow", DisallowKeywordValidator.getInstance(),
            values());

        /* divisibleBy */
        registerSV("divisibleBy", DivisibleBySyntaxValidator.getInstance());
        registerKV("divisibleBy", DivisibleByKeywordValidator.getInstance(),
            INTEGER, NUMBER);

        /* enum */
        registerSV("enum", EnumSyntaxValidator.getInstance());
        registerKV("enum", EnumKeywordValidator.getInstance(), values());

        /* exclusiveMaximum */
        registerSV("exclusiveMaximum",
            ExclusiveMaximumSyntaxValidator.getInstance());
        registerIgnoredKV("exclusiveMaximum", INTEGER, NUMBER);

        /* exclusiveMinimum */
        registerSV("exclusiveMinimum",
            ExclusiveMinimumSyntaxValidator.getInstance());
        registerIgnoredKV("exclusiveMinimum", INTEGER, NUMBER);

        /* extends */
        registerSV("extends", ExtendsSyntaxValidator.getInstance());
        registerKV("extends", ExtendsKeywordValidator.getInstance(), values());

        /* format */
        registerSV("format", FormatSyntaxValidator.getInstance());
        registerKV("format", FormatKeywordValidator.getInstance(), values());

        /* id */
        registerSV("id", IdSyntaxValidator.getInstance());
        registerIgnoredKV("id", values());

        /* items */
        registerSV("items", ItemsSyntaxValidator.getInstance());
        registerIgnoredKV("items", ARRAY);

        /* links */
        registerIgnoredSV("links");
        registerIgnoredKV("links", values());

        /* maximum */
        registerSV("maximum", MaximumSyntaxValidator.getInstance());
        registerKV("maximum", MaximumKeywordValidator.getInstance(), INTEGER,
            NUMBER);

        /* maxItems */
        registerSV("maxItems", MaxItemsSyntaxValidator.getInstance());
        registerKV("maxItems", MaxItemsKeywordValidator.getInstance(), ARRAY);

        /* maxLength */
        registerSV("maxLength", MaxLengthSyntaxValidator.getInstance());
        registerKV("maxLength", MaxLengthKeywordValidator.getInstance(),
            STRING);

        /* minimum */
        registerSV("minimum", MinimumSyntaxValidator.getInstance());
        registerKV("minimum", MinimumKeywordValidator.getInstance(), INTEGER,
            NUMBER);

        /* minItems */
        registerSV("minItems", MinItemsSyntaxValidator.getInstance());
        registerKV("minItems", MinItemsKeywordValidator.getInstance(), ARRAY);

        /* minLength */
        registerSV("minLength", MinLengthSyntaxValidator.getInstance());
        registerKV("minLength", MinLengthKeywordValidator.getInstance(),
            STRING);

        /* pattern */
        registerSV("pattern", PatternSyntaxValidator.getInstance());
        registerKV("pattern", PatternKeywordValidator.getInstance(), STRING);

        /* patternProperties */
        registerSV("patternProperties",
            PatternPropertiesSyntaxValidator.getInstance());
        registerIgnoredKV("patternProperties", OBJECT);

        /* properties: left to subclasses */
        /* required: left to subclasses */

        /* title */
        registerSV("title", TitleSyntaxValidator.getInstance());
        registerIgnoredKV("title", values());

        /* type */
        registerSV("type", TypeSyntaxValidator.getInstance());
        registerKV("type", TypeKeywordValidator.getInstance(), values());

        /* uniqueItems */
        registerSV("uniqueItems", UniqueItemsSyntaxValidator.getInstance());
        registerKV("uniqueItems", UniqueItemsKeywordValidator.getInstance(),
            ARRAY);

        /* $ref */
        /*
         * Unfortunately, syntax validation differs between draft v3 and v4:
         * in v3, $ref may be paired with required -- in v4, no.
         */
        registerKV("$ref", RefKeywordValidator.getInstance(), values());

        /* $schema */
        registerSV("$schema", DollarSchemaSyntaxValidator.getInstance());
        registerIgnoredKV("$schema", values());
    }
}
