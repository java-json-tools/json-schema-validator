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
