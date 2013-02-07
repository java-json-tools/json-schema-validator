/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.library.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.common.EnumKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.AdditionalItemsKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.AdditionalPropertiesKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MaxItemsKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MaxLengthKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MaximumKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinItemsKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinLengthKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinimumKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.PatternKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.UniqueItemKeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

import java.lang.reflect.Constructor;

public final class CommonValidatorDictionary
{
    private static final Dictionary<Constructor<? extends KeywordValidator>>
        DICTIONARY;

    private CommonValidatorDictionary()
    {
    }

    public static Dictionary<Constructor<? extends KeywordValidator>> get()
    {
        return DICTIONARY;
    }

    static {
        final DictionaryBuilder<Constructor<? extends KeywordValidator>>
            builder = Dictionary.newBuilder();

        String keyword;
        Class<? extends KeywordValidator> c;

        /*
         * Arrays
         */
        keyword = "additionalItems";
        c = AdditionalItemsKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "minItems";
        c = MinItemsKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maxItems";
        c = MaxItemsKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "uniqueItems";
        c = UniqueItemKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Numbers and integers
         */
        keyword = "minimum";
        c = MinimumKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maximum";
        c = MaximumKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Objects
         */
        keyword = "additionalProperties";
        c = AdditionalPropertiesKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Strings
         */
        keyword = "minLength";
        c = MinLengthKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maxLength";
        c = MaxLengthKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "pattern";
        c = PatternKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "enum";
        c = EnumKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        DICTIONARY = builder.freeze();
    }

    private static Constructor<? extends KeywordValidator> constructor(
        final Class<? extends KeywordValidator> c)
    {
        try {
            return c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No appropriate constructor", e);
        }
    }
}
