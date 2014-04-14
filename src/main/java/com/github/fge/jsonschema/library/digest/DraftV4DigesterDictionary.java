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
import com.github.fge.jsonschema.keyword.digest.draftv4.DraftV4DependenciesDigester;
import com.github.fge.jsonschema.keyword.digest.draftv4.DraftV4TypeDigester;
import com.github.fge.jsonschema.keyword.digest.draftv4.MultipleOfDigester;
import com.github.fge.jsonschema.keyword.digest.draftv4.RequiredDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.NullDigester;
import com.github.fge.jsonschema.keyword.digest.helpers.SimpleDigester;

import static com.github.fge.jackson.NodeType.*;

/**
 * Draft v4 specific digesters
 */
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
