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

package org.eel.kitchen.jsonschema.metaschema;

import com.google.common.collect.ImmutableMap;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.AdditionalItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.AdditionalPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.EnumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.FormatKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaxItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaxLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MaximumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinLengthKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.MinimumKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.PatternKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.UniqueItemsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.DisallowKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.DivisibleByKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.DraftV3DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.DraftV3PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.DraftV3TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv3.ExtendsKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.AllOfKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.AnyOfKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.DraftV4DependenciesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.DraftV4TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.MaxPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.MinPropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.MultipleOfKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.NotKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.OneOfKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.draftv4.RequiredKeywordValidator;

import java.util.Map;

/**
 * Utility class for builtin keyword validators
 *
 * <p>As for other similar classes, it provides methods to retrieve validators
 * defined by draft v3 and draft v4.</p>
 */

public final class KeywordValidators
{
    private static final Map<String, Class<? extends KeywordValidator>> DRAFTV3;
    private static final Map<String, Class<? extends KeywordValidator>> DRAFTV4;

    // No making new instances of this class
    private KeywordValidators()
    {
    }

    static {
        ImmutableMap.Builder<String, Class<? extends KeywordValidator>> builder;

        String keyword;
        Class<? extends KeywordValidator> validator;

        /*
         * Common keyword validators
         */
        builder = ImmutableMap.builder();

        // Array
        keyword = "additionalItems";
        validator = AdditionalItemsKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "minItems";
        validator = MinItemsKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "maxItems";
        validator = MaxItemsKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "uniqueItems";
        validator = UniqueItemsKeywordValidator.class;
        builder.put(keyword, validator);

        // Integer/number
        keyword = "minimum";
        validator = MinimumKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "maximum";
        validator = MaximumKeywordValidator.class;
        builder.put(keyword, validator);

        // Object
        keyword = "additionalProperties";
        validator = AdditionalPropertiesKeywordValidator.class;
        builder.put(keyword, validator);

        // String
        keyword = "minLength";
        validator = MinLengthKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "maxLength";
        validator = MaxLengthKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "pattern";
        validator = PatternKeywordValidator.class;
        builder.put(keyword, validator);

        // All
        keyword = "enum";
        validator = EnumKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "format";
        validator = FormatKeywordValidator.class;
        builder.put(keyword, validator);

        // Build the map
        final Map<String, Class<? extends KeywordValidator>> common
            = builder.build();

        /*
         * Draft v3 specific keyword validators
         */
        builder = ImmutableMap.builder();

        // Inject all common validators
        builder.putAll(common);

        // Now inject all draft v3 specific validators

        // Integer/number
        keyword = "divisibleBy";
        validator = DivisibleByKeywordValidator.class;
        builder.put(keyword, validator);

        // Object
        keyword = "properties";
        validator = DraftV3PropertiesKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "dependencies";
        validator = DraftV3DependenciesKeywordValidator.class;
        builder.put(keyword, validator);

        // All
        keyword = "type";
        validator = DraftV3TypeKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "disallow";
        validator = DisallowKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "extends";
        validator = ExtendsKeywordValidator.class;
        builder.put(keyword, validator);

        // Build the map
        DRAFTV3 = builder.build();

        /*
         * Draft v4 specific keyword validators
         */
        builder = ImmutableMap.builder();

        // Inject all common validators
        builder.putAll(common);

        // Now inject all draft v3 specific validators

        // Integer/number
        keyword = "multipleOf";
        validator = MultipleOfKeywordValidator.class;
        builder.put(keyword, validator);

        // Object
        keyword = "minProperties";
        validator = MinPropertiesKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "maxProperties";
        validator = MaxPropertiesKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "required";
        validator = RequiredKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "dependencies";
        validator = DraftV4DependenciesKeywordValidator.class;
        builder.put(keyword, validator);

        // All/none
        keyword = "anyOf";
        validator = AnyOfKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "allOf";
        validator = AllOfKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "oneOf";
        validator = OneOfKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "not";
        validator = NotKeywordValidator.class;
        builder.put(keyword, validator);

        keyword = "type";
        validator = DraftV4TypeKeywordValidator.class;
        builder.put(keyword, validator);

        // Build the map
        DRAFTV4 = builder.build();
    }

    /**
     * Return an immutable copy of keyword validators for draft v3
     *
     * @return a map pairing keyword names and their validator classes
     */
    public static Map<String, Class<? extends KeywordValidator>> draftV3()
    {
        return DRAFTV3;
    }

    /**
     * Return an immutable copy of keyword validators for draft v4
     *
     * @return a map pairing keyword names and their validator classes
     */
    public static Map<String, Class<? extends KeywordValidator>> draftV4()
    {
        return DRAFTV4;
    }
}
