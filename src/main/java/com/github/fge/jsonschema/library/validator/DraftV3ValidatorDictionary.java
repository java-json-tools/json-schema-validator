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
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.common.DependenciesValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.DisallowKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.DivisibleByValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.DraftV3TypeValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.ExtendsValidator;
import com.github.fge.jsonschema.keyword.validator.draftv3.PropertiesValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

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
