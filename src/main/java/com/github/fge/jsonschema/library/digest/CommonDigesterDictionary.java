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

import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.keyword.digest.common.AdditionalItemsDigester;
import com.github.fge.jsonschema.keyword.digest.common.AdditionalPropertiesDigester;
import com.github.fge.jsonschema.keyword.digest.common.MaximumDigester;
import com.github.fge.jsonschema.keyword.digest.common.MinimumDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.NullDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.SimpleDigester;

import static com.github.fge.jackson.NodeType.*;

/**
 * Digesters common to draft v4 and v3
 */
public final class CommonDigesterDictionary
{
    private static final Dictionary<Digester> DICTIONARY;

    private CommonDigesterDictionary()
    {
    }

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
        digester = new SimpleDigester(keyword, ARRAY);
        builder.addEntry(keyword, digester);

        keyword = "maxItems";
        digester = new SimpleDigester(keyword, ARRAY);
        builder.addEntry(keyword, digester);

        keyword = "uniqueItems";
        digester = new SimpleDigester(keyword, ARRAY);
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

        /*
         * String
         */
        keyword = "minLength";
        digester = new SimpleDigester(keyword, STRING);
        builder.addEntry(keyword, digester);

        keyword = "maxLength";
        digester = new SimpleDigester(keyword, STRING);
        builder.addEntry(keyword, digester);

        keyword = "pattern";
        digester = new NullDigester(keyword, STRING);
        builder.addEntry(keyword, digester);

        /*
         * Any
         */

        /*
         * FIXME: not perfect
         *
         * Right now we take the node as is, and all the real work is done by
         * the validator. That is:
         *
         * - { "enum": [ 1 ] } and { "enum": [ 1.0 ] } are not the same;
         * - { "enum": [ 1, 2 ] } and { "enum": [ 2, 1 ] } are not the same
         *   either.
         *
         * All these differences are sorted out by the runtime checking, not
         * here. This is kind of a waste, but making just these two above
         * examples yield the same digest would require not only normalizing
         * (for the first case), but also ordering (for the second case).
         *
         * And we don't even get into the territory of other node types here.
         *
         * Bah. There will be duplicates, but at least ultimately the validator
         * will do what it takes.
         */
        keyword = "enum";
        digester = new SimpleDigester(keyword, ARRAY, values());
        builder.addEntry(keyword, digester);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<Digester> get()
    {
        return DICTIONARY;
    }
}
