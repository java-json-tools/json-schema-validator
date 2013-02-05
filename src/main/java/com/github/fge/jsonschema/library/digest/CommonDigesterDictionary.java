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

package com.github.fge.jsonschema.library.digest;

import com.github.fge.jsonschema.keyword.digest.AdditionalPropertiesDigester;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.SimpleDigester;
import com.github.fge.jsonschema.keyword.digest.common.AdditionalItemsDigester;
import com.github.fge.jsonschema.keyword.digest.common.MaximumDigester;
import com.github.fge.jsonschema.keyword.digest.common.MinimumDigester;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.util.NodeType;

public final class CommonDigesterDictionary
{
    private static final Dictionary<Digester> DICTIONARY;

    static {
        final DictionaryBuilder<Digester> builder
            = Dictionary.newBuilder();

        String keyword;
        Digester digester;

        /*
         * Array
         */
        keyword = "additionalItems";
        digester = AdditionalItemsDigester.getInstance();
        builder.addEntry(keyword, digester);

        keyword = "minItems";
        digester = new SimpleDigester(keyword, NodeType.ARRAY);
        builder.addEntry(keyword, digester);

        keyword = "maxItems";
        digester = new SimpleDigester(keyword, NodeType.ARRAY);
        builder.addEntry(keyword, digester);

        keyword = "uniqueItems";
        digester = new SimpleDigester(keyword, NodeType.ARRAY);
        builder.addEntry(keyword, digester);

        /*
         * Number / Integer
         */
        keyword = "minimum";
        digester = MinimumDigester.getInstance();
        builder.addEntry(keyword, digester);

        keyword = "maximum";
        digester = MaximumDigester.getInstance();
        builder.addEntry(keyword, digester);

        /*
         * Object
         */
        keyword = "additionalProperties";
        digester = AdditionalPropertiesDigester.getInstance();
        builder.addEntry(keyword, digester);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<Digester> get()
    {
        return DICTIONARY;
    }
}
