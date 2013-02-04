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

package com.github.fge.jsonschema.library.keyword;

import com.github.fge.jsonschema.keyword.equivalences.common.AdditionalItemsEquivalence;
import com.github.fge.jsonschema.keyword.equivalences.PositiveIntegerEquivalence;
import com.github.fge.jsonschema.keyword.equivalences.common.UniqueItemsEquivalence;
import com.github.fge.jsonschema.keyword.validators.common.AdditionalItemsKeywordValidator;
import com.github.fge.jsonschema.keyword.validators.common.MaxItemsKeywordValidator;
import com.github.fge.jsonschema.keyword.validators.common.MinItemsKeywordValidator;
import com.github.fge.jsonschema.keyword.validators.common.UniqueItemKeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptorBuilder;

import static com.github.fge.jsonschema.util.NodeType.*;

public final class CommonKeywordValidatorDictionary
{
    private static final Dictionary<KeywordDescriptor> DICTIONARY;

    private CommonKeywordValidatorDictionary()
    {
    }

    public static Dictionary<KeywordDescriptor> get()
    {
        return DICTIONARY;
    }

    static {
        final DictionaryBuilder<KeywordDescriptor> builder
            = Dictionary.newBuilder();

        String keyword;
        KeywordDescriptorBuilder descriptor;

        /*
         * Arrays
         */
        keyword = "additionalItems";
        descriptor = KeywordDescriptor.newBuilder().setValidatedTypes(ARRAY)
            .setValidatorClass(AdditionalItemsKeywordValidator.class)
            .setSchemaEquivalence(AdditionalItemsEquivalence.getInstance());
        builder.addEntry(keyword, descriptor.freeze());

        keyword = "minItems";
        descriptor = KeywordDescriptor.newBuilder().setValidatedTypes(ARRAY)
            .setValidatorClass(MinItemsKeywordValidator.class)
            .setSchemaEquivalence(new PositiveIntegerEquivalence(keyword));
        builder.addEntry(keyword, descriptor.freeze());

        keyword = "maxItems";
        descriptor = KeywordDescriptor.newBuilder().setValidatedTypes(ARRAY)
            .setValidatorClass(MaxItemsKeywordValidator.class)
            .setSchemaEquivalence(new PositiveIntegerEquivalence(keyword));
        builder.addEntry(keyword, descriptor.freeze());

        keyword = "uniqueItems";
        descriptor = KeywordDescriptor.newBuilder().setValidatedTypes(ARRAY)
            .setValidatorClass(UniqueItemKeywordValidator.class)
            .setSchemaEquivalence(UniqueItemsEquivalence.getInstance());
        builder.addEntry(keyword, descriptor.freeze());

        DICTIONARY = builder.freeze();
    }
}
