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

import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.DraftV4TypeDigester;
import com.github.fge.jsonschema.keyword.digest.NullDigester;
import com.github.fge.jsonschema.keyword.digest.SimpleDigester;
import com.github.fge.jsonschema.keyword.digest.draftv4.DraftV4DependenciesDigester;
import com.github.fge.jsonschema.keyword.digest.draftv4.MultipleOfDigester;
import com.github.fge.jsonschema.keyword.digest.draftv4.RequiredDigester;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

import static com.github.fge.jsonschema.util.NodeType.*;

public final class DraftV4DigesterDictionary
{
    private static final Dictionary<Digester> DICTIONARY;

    private DraftV4DigesterDictionary()
    {
    }

    static {
        final DictionaryBuilder<Digester> builder
            = Dictionary.newBuilder();

        String keyword;
        Digester digester;

        builder.addAll(CommonDigesterDictionary.get());

        /*
         * Number/integer
         */
        keyword = "multipleOf";
        digester = MultipleOfDigester.getInstance();
        builder.addEntry(keyword, digester);

        /*
         * Object
         */
        keyword = "minProperties";
        digester = new SimpleDigester(keyword, OBJECT);
        builder.addEntry(keyword, digester);

        keyword = "maxProperties";
        digester = new SimpleDigester(keyword, OBJECT);
        builder.addEntry(keyword, digester);

        keyword = "required";
        digester = RequiredDigester.getInstance();
        builder.addEntry(keyword, digester);

        keyword = "dependencies";
        digester = DraftV4DependenciesDigester.getInstance();
        builder.addEntry(keyword, digester);

        /*
         * All/none
         */
        keyword = "anyOf";
        digester = new NullDigester(keyword, ARRAY, values());
        builder.addEntry(keyword, digester);

        keyword = "allOf";
        digester = new NullDigester(keyword, ARRAY, values());
        builder.addEntry(keyword, digester);

        keyword = "oneOf";
        digester = new NullDigester(keyword, ARRAY, values());
        builder.addEntry(keyword, digester);

        keyword = "not";
        digester = new NullDigester(keyword, ARRAY, values());
        builder.addEntry(keyword, digester);

        keyword = "type";
        digester = DraftV4TypeDigester.getInstance();
        builder.addEntry(keyword, digester);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<Digester> get()
    {
        return DICTIONARY;
    }
}
