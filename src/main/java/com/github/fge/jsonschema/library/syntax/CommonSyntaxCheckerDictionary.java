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
import com.github.fge.jsonschema.keyword.syntax.common.AdditionalSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.common.EnumSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.common.ExclusiveMaximumSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.common.ExclusiveMinimumSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.common.PatternPropertiesSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.common.PatternSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.PositiveIntegerSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.TypeOnlySyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.URISyntaxChecker;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

import static com.github.fge.jsonschema.util.NodeType.*;

/**
 * Syntax checkers common to draft v4 and v3
 */
public final class CommonSyntaxCheckerDictionary
{
    private static final Dictionary<SyntaxChecker> DICTIONARY;

    public static Dictionary<SyntaxChecker> get()
    {
        return DICTIONARY;
    }

    private CommonSyntaxCheckerDictionary()
    {
    }

    static {
        final DictionaryBuilder<SyntaxChecker> dict = Dictionary.newBuilder();

        String keyword;
        SyntaxChecker checker;

        /*
         * Arrays
         */

        keyword = "additionalItems";
        checker = new AdditionalSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "minItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "maxItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "uniqueItems";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        dict.addEntry(keyword, checker);

        /*
         * Integers and numbers
         */
        keyword = "minimum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        dict.addEntry(keyword, checker);

        keyword = "exclusiveMinimum";
        checker = ExclusiveMinimumSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        keyword = "maximum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        dict.addEntry(keyword, checker);

        keyword = "exclusiveMaximum";
        checker = ExclusiveMaximumSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        /*
         * Objects
         */
        keyword = "additionalProperties";
        checker = new AdditionalSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "patternProperties";
        checker = PatternPropertiesSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        keyword = "required";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        dict.addEntry(keyword, checker);

        /*
         * Strings
         */
        keyword = "minLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "maxLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "pattern";
        checker = PatternSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        /*
         * All/metadata
         */
        keyword = "$schema";
        checker = new URISyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "$ref";
        checker = new URISyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "id";
        checker = new URISyntaxChecker(keyword);
        dict.addEntry(keyword, checker);

        keyword = "description";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        dict.addEntry(keyword, checker);

        keyword = "title";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        dict.addEntry(keyword, checker);

        keyword = "enum";
        checker = EnumSyntaxChecker.getInstance();
        dict.addEntry(keyword, checker);

        keyword = "format";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        dict.addEntry(keyword, checker);

        // FIXME: we actually ignore this one
        keyword = "default";
        checker = new TypeOnlySyntaxChecker(keyword, ARRAY, values());
        dict.addEntry(keyword, checker);

        DICTIONARY = dict.freeze();
    }
}
