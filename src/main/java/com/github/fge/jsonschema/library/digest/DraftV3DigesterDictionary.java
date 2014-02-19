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

import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.draftv3.DivisibleByDigester;
import com.github.fge.jsonschema.keyword.digest.draftv3.DraftV3DependenciesDigester;
import com.github.fge.jsonschema.keyword.digest.draftv3.DraftV3PropertiesDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.DraftV3TypeKeywordDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.NullDigester;

/**
 * Draft v3 specific digesters
 */
public final class DraftV3DigesterDictionary
{
    private static final Dictionary<Digester> DICTIONARY;

    private DraftV3DigesterDictionary()
    {
    }

    static {
        final DictionaryBuilder<Digester> builder
            = Dictionary.newBuilder();

        String keyword;
        Digester digester;

        builder.addAll(CommonDigesterDictionary.get());

        /*
         * Number / integer
         */
        keyword = "divisibleBy";
        digester = DivisibleByDigester.getInstance();
        builder.addEntry(keyword, digester);

        /*
         * Object
         */
        keyword = "properties";
        digester = DraftV3PropertiesDigester.getInstance();
        builder.addEntry(keyword, digester);

        keyword = "dependencies";
        digester = DraftV3DependenciesDigester.getInstance();
        builder.addEntry(keyword, digester);

        /*
         * All
         */
        keyword = "type";
        digester = new DraftV3TypeKeywordDigester(keyword);
        builder.addEntry(keyword, digester);

        keyword = "disallow";
        digester = new DraftV3TypeKeywordDigester(keyword);
        builder.addEntry(keyword, digester);

        keyword = "extends";
        digester = new NullDigester(keyword, NodeType.ARRAY, NodeType.values());
        builder.addEntry(keyword, digester);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<Digester> get()
    {
        return DICTIONARY;
    }
}
