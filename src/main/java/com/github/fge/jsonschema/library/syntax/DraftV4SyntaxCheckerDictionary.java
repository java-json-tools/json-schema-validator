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
import com.github.fge.jsonschema.keyword.syntax.draftv4.DefinitionsSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv4.DraftV4DependenciesSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv4.DraftV4ItemsSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv4.DraftV4PropertiesSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv4.DraftV4TypeSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv4.NotSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.draftv4.RequiredSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.DivisorSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.PositiveIntegerSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.SchemaArraySyntaxChecker;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

/**
 * Draft v4 specific syntax checkers
 */
public final class DraftV4SyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private DraftV4SyntaxCheckerDictionary()
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
         * Array
         */
        keyword = "items";
        checker = DraftV4ItemsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * Integers and numbers
         */
        keyword = "multipleOf";
        checker = new DivisorSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        /*
         * Objects
         */
        keyword = "minProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "maxProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "properties";
        checker = DraftV4PropertiesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "required";
        checker = RequiredSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "dependencies";
        checker = DraftV4DependenciesSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        /*
         * All / metadata
         */
        keyword = "allOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "anyOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "oneOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.addEntry(keyword, checker);

        keyword = "not";
        checker = NotSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "definitions";
        checker = DefinitionsSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        keyword = "type";
        checker = DraftV4TypeSyntaxChecker.getInstance();
        builder.addEntry(keyword, checker);

        DICTIONARY = builder.freeze();
    }
}
