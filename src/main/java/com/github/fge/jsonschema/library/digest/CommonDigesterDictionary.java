/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
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
