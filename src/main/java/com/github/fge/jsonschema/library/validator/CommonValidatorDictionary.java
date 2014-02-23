/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.library.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.AdditionalItemsValidator;
import com.github.fge.jsonschema.keyword.validator.common.AdditionalPropertiesValidator;
import com.github.fge.jsonschema.keyword.validator.common.EnumValidator;
import com.github.fge.jsonschema.keyword.validator.common.MaxItemsValidator;
import com.github.fge.jsonschema.keyword.validator.common.MaxLengthValidator;
import com.github.fge.jsonschema.keyword.validator.common.MaximumValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinItemsValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinLengthValidator;
import com.github.fge.jsonschema.keyword.validator.common.MinimumValidator;
import com.github.fge.jsonschema.keyword.validator.common.PatternValidator;
import com.github.fge.jsonschema.keyword.validator.common.UniqueItemsValidator;

import java.lang.reflect.Constructor;

/**
 * Keyword validator constructors common to draft v4 and v3
 */
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
        c = AdditionalItemsValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "minItems";
        c = MinItemsValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maxItems";
        c = MaxItemsValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "uniqueItems";
        c = UniqueItemsValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Numbers and integers
         */
        keyword = "minimum";
        c = MinimumValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maximum";
        c = MaximumValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Objects
         */
        keyword = "additionalProperties";
        c = AdditionalPropertiesValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Strings
         */
        keyword = "minLength";
        c = MinLengthValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maxLength";
        c = MaxLengthValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "pattern";
        c = PatternValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "enum";
        c = EnumValidator.class;
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
