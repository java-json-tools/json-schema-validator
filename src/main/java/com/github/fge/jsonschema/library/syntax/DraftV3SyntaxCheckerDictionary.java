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

package com.github.fge.jsonschema.library.syntax;

import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv3.DraftV3DependenciesSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv3.DraftV3ItemsSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv3.DraftV3PropertiesSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv3.ExtendsSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.DivisorSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.DraftV3TypeKeywordSyntaxChecker;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

/**
 * Draft v3 specific syntax checkers
 */
public final class DraftV3SyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private DraftV3SyntaxCheckerDictionary()
    {
    }

    static {
        final DictionaryBuilder<SyntaxChecker> builder
            = Dictionary.newBuilder();

        /*
         * Put all common checkers
         */
        builder.addAll(CommonSyntaxCheckerDictionary.get());

        String keyword;
        SyntaxChecker checker;

        /*
         * Draft v3 specific syntax checkers
         */

        /*
         * Arrays
         */
        keyword = "items";
        checker = DraftV3ItemsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * Numbers and integers
         */
        keyword = "divisibleBy";
        checker = new DivisorSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        /*
         * Objects
         */
        keyword = "properties";
        checker = DraftV3PropertiesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "dependencies";
        checker = DraftV3DependenciesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * All / metadata
         */
        keyword = "extends";
        checker = ExtendsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "type";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "disallow";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        DICTIONARY = builder.freeze();
    }
}
