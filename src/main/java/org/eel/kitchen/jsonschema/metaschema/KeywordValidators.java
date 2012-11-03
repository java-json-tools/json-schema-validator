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
import org.eel.kitchen.jsonschema.keyword.PropertiesKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.TypeKeywordValidator;
import org.eel.kitchen.jsonschema.keyword.UniqueItemsKeywordValidator;

import java.util.Map;

public final class KeywordValidators
{
    private static final Map<String, Class<? extends KeywordValidator>> DRAFTV3;

    // No making new instances of this class
    private KeywordValidators()
    {
    }

    static {
        final MapBuilder<Class<? extends KeywordValidator>> common
            = MapBuilder.create();

        String keyword;
        Class<? extends KeywordValidator> validator;

        /*
         * Common keyword validators
         */
        // Array
        keyword = "additionalItems";
        validator = AdditionalItemsKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "minItems";
        validator = MinItemsKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "maxItems";
        validator = MaxItemsKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "uniqueItems";
        validator = UniqueItemsKeywordValidator.class;
        common.put(keyword, validator);

        // Integer/number
        keyword = "minimum";
        validator = MinimumKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "maximum";
        validator = MaximumKeywordValidator.class;
        common.put(keyword, validator);

        // Object
        keyword = "additionalProperties";
        validator = AdditionalPropertiesKeywordValidator.class;
        common.put(keyword, validator);

        // String
        keyword = "minLength";
        validator = MinLengthKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "maxLength";
        validator = MaxLengthKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "pattern";
        validator = PatternKeywordValidator.class;
        common.put(keyword, validator);

        // All
        keyword = "enum";
        validator = EnumKeywordValidator.class;
        common.put(keyword, validator);

        keyword = "format";
        validator = FormatKeywordValidator.class;
        common.put(keyword, validator);

        // Build the map
        final Map<String, Class<? extends KeywordValidator>> commonMap
            = common.build();

        /*
         * Draft v3 specific keyword validators
         */
        final MapBuilder<Class<? extends KeywordValidator>> draftv3
            = MapBuilder.create();

        // Integer/number
        keyword = "divisibleBy";
        validator = DivisibleByKeywordValidator.class;
        draftv3.put(keyword, validator);

        // Object
        keyword = "properties";
        validator = PropertiesKeywordValidator.class;
        draftv3.put(keyword, validator);

        keyword = "dependencies";
        validator = DependenciesKeywordValidator.class;
        draftv3.put(keyword, validator);

        // All
        keyword = "type";
        validator = TypeKeywordValidator.class;
        draftv3.put(keyword, validator);

        keyword = "disallow";
        validator = DisallowKeywordValidator.class;
        draftv3.put(keyword, validator);

        keyword = "extends";
        validator = ExtendsKeywordValidator.class;
        draftv3.put(keyword, validator);

        // Build the map
        draftv3.putAll(commonMap);
        DRAFTV3 = draftv3.build();
    }

    public static Map<String, Class<? extends KeywordValidator>> draftV3()
    {
        return DRAFTV3;
    }
}
