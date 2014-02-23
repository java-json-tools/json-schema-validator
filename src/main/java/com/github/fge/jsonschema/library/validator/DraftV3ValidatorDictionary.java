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
import com.github.fge.jsonschema.keyword.validator.common.DependenciesValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.DisallowKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.DivisibleByValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.DraftV3TypeValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.ExtendsValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.PropertiesValidator;

import java.lang.reflect.Constructor;

/**
 * Draft v3 specific keyword validator constructors
 */
public final class DraftV3ValidatorDictionary
{
    private static final Dictionary<Constructor<? extends KeywordValidator>>
        DICTIONARY;

    private DraftV3ValidatorDictionary()
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

        builder.addAll(CommonValidatorDictionary.get());

        /*
         * Number / integer
         */
        keyword = "divisibleBy";
        c = DivisibleByValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Object
         */
        keyword = "properties";
        c = PropertiesValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "dependencies";
        c = DependenciesValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "type";
        c = DraftV3TypeValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "disallow";
        c = DisallowKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "extends";
        c = ExtendsValidator.class;
        builder.addEntry(keyword, constructor(c));

        DICTIONARY = builder.freeze();
    }

    private static Constructor<? extends KeywordValidator> constructor(
        final Class<? extends KeywordValidator> c)
    {
        try {
            return c.getConstructor(JsonNode.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No appropriate constructor found", e);
        }
    }
}
