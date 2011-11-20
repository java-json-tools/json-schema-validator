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
public abstract class CommonValidatorBundle
    extends ValidatorBundle
{
    CommonValidatorBundle()
    {
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
}
