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
import com.github.fge.jsonschema.keyword.validator.common.DependenciesKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.DraftV4TypeKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.MaxPropertiesKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.MinPropertiesKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.MultipleOfKeywordValidator;
import com.github.fge.jsonschema.keyword.validator.draftv4.RequiredKeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

import java.lang.reflect.Constructor;

public final class DraftV4ValidatorDictionary
{
    private static final Dictionary<Constructor<? extends KeywordValidator>>
        DICTIONARY;

    private DraftV4ValidatorDictionary()
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
         * Number/integer
         */
        keyword = "multipleOf";
        c = MultipleOfKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        /*
         * Object
         */
        keyword = "minProperties";
        c = MinPropertiesKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "maxProperties";
        c = MaxPropertiesKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "required";
        c = RequiredKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "dependencies";
        c = DependenciesKeywordValidator.class;
        builder.addEntry(keyword, constructor(c));

        keyword = "type";
        c = DraftV4TypeKeywordValidator.class;
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
