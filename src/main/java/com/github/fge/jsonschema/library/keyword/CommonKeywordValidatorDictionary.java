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

import com.github.fge.jsonschema.keyword.common.AdditionalItemsKeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptorBuilder;
import com.github.fge.jsonschema.util.NodeType;

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
        KeywordDescriptorBuilder desc;

        /*
         * Arrays
         */
        keyword = "additionalItems";
        desc = KeywordDescriptor.newBuilder()
            .setValidatedTypes(NodeType.ARRAY)
            .setValidatorClass(AdditionalItemsKeywordValidator.class);
        builder.addEntry(keyword, desc.freeze());

        DICTIONARY = builder.freeze();
    }
}
