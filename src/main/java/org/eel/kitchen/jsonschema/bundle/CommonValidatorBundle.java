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

import org.eel.kitchen.jsonschema.keyword.common.AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.AdditionalPropertiesKeywordValidator;
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
import org.eel.kitchen.jsonschema.syntax.common.AdditionalPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DependenciesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DescriptionSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DisallowSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DivisibleBySyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DollarRefSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.DollarSchemaSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.EnumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.ExclusiveMaximumSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.ExclusiveMinimumSyntaxValidator;
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
import org.eel.kitchen.jsonschema.syntax.common.PatternPropertiesSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.PatternSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.TitleSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.TypeSyntaxValidator;
import org.eel.kitchen.jsonschema.syntax.common.UniqueItemsSyntaxValidator;

import static org.eel.kitchen.util.NodeType.*;

/**
 * The common set of validators used by existing JSON Schema specifications
 */
public class CommonValidatorBundle
    extends ValidatorBundle
{
    CommonValidatorBundle()
    {

        /* additionalItems */
        registerSV("additionalItems", new AdditionalItemsSyntaxValidator());
        registerKV("additionalItems", new AdditionalItemsKeywordValidator(),
            ARRAY);

        /* additionalProperties */
        registerSV("additionalProperties",
            new AdditionalPropertiesSyntaxValidator());
        registerKV("additionalProperties",
            new AdditionalPropertiesKeywordValidator(), OBJECT);

        /* default */
        registerIgnoredSV("default");
        registerIgnoredKV("default", values());

        /* dependencies */
        registerSV("dependencies", new DependenciesSyntaxValidator());
        registerKV("dependencies", new DependenciesKeywordValidator(),
            values());

        /* description */
        registerSV("description", new DescriptionSyntaxValidator());
        registerIgnoredKV("description", values());

        /* disallow */
        registerSV("disallow", new DisallowSyntaxValidator());
        registerKV("disallow", new DisallowKeywordValidator(), values());

        /* divisibleBy */
        registerSV("divisibleBy", new DivisibleBySyntaxValidator());
        registerKV("divisibleBy", new DivisibleByKeywordValidator(), INTEGER,
            NUMBER);

        /* enum */
        registerSV("enum", new EnumSyntaxValidator());
        registerKV("enum", new EnumKeywordValidator(), values());

        /* exclusiveMaximum */
        registerSV("exclusiveMaximum", new ExclusiveMaximumSyntaxValidator());
        registerIgnoredKV("exclusiveMaximum", INTEGER, NUMBER);

        /* exclusiveMinimum */
        registerSV("exclusiveMinimum", new ExclusiveMinimumSyntaxValidator());
        registerIgnoredKV("exclusiveMinimum", INTEGER, NUMBER);

        /* extends */
        registerSV("extends", new ExtendsSyntaxValidator());
        registerKV("extends", new ExtendsKeywordValidator(), values());

        /* format */
        registerSV("format", new FormatSyntaxValidator());
        registerKV("format", new FormatKeywordValidator(), values());

        /* id */
        registerSV("id", new IdSyntaxValidator());
        registerIgnoredKV("id", values());

        /* items */
        registerSV("items", new ItemsSyntaxValidator());
        registerIgnoredKV("items", ARRAY);

        /* links */
        registerIgnoredSV("links");
        registerIgnoredKV("links", values());

        /* maximum */
        registerSV("maximum", new MaximumSyntaxValidator());
        registerKV("maximum", new MaximumKeywordValidator(), INTEGER, NUMBER);

        /* maxItems */
        registerSV("maxItems", new MaxItemsSyntaxValidator());
        registerKV("maxItems", new MaxItemsKeywordValidator(), ARRAY);

        /* maxLength */
        registerSV("maxLength", new MaxLengthSyntaxValidator());
        registerKV("maxLength", new MaxLengthKeywordValidator(), STRING);

        /* minimum */
        registerSV("minimum", new MinimumSyntaxValidator());
        registerKV("minimum", new MinimumKeywordValidator(), INTEGER, NUMBER);

        /* minItems */
        registerSV("minItems", new MinItemsSyntaxValidator());
        registerKV("minItems", new MinItemsKeywordValidator(), ARRAY);

        /* minLength */
        registerSV("minLength", new MinLengthSyntaxValidator());
        registerKV("minLength", new MinLengthKeywordValidator(), STRING);

        /* pattern */
        registerSV("pattern", new PatternSyntaxValidator());
        registerKV("pattern", new PatternKeywordValidator(), STRING);

        /* patternProperties */
        registerSV("patternProperties", new PatternPropertiesSyntaxValidator());
        registerIgnoredKV("patternProperties", OBJECT);

        /* properties: left to subclasses */
        /* required: left to subclasses */

        /* title */
        registerSV("title", new TitleSyntaxValidator());
        registerIgnoredKV("title", values());

        /* type */
        registerSV("type", new TypeSyntaxValidator());
        registerKV("type", new TypeKeywordValidator(), values());

        /* uniqueItems */
        registerSV("uniqueItems", new UniqueItemsSyntaxValidator());
        registerKV("uniqueItems", new UniqueItemsKeywordValidator(), ARRAY);

        /* $ref */
        registerSV("$ref", new DollarRefSyntaxValidator());
        registerKV("$ref", new RefKeywordValidator(), values());

        /* $schema */
        registerSV("$schema", new DollarSchemaSyntaxValidator());
        registerIgnoredKV("$schema", values());
    }
}
