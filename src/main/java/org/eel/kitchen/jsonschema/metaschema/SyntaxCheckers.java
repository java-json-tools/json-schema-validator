/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.metaschema;

import org.eel.kitchen.jsonschema.syntax.DivisorSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.PositiveIntegerSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.TypeOnlySyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.EnumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.ExclusiveMaximumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.ExclusiveMinimumSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.PatternPropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.PatternSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.URISyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv3.DraftV3DependenciesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv3.DraftV3ItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv3.DraftV3PropertiesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv3.DraftV3TypeKeywordSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv3.ExtendsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv4.DraftV4DependenciesSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv4.DraftV4ItemsSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv4.DraftV4TypeSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv4.RequiredSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.draftv4.SchemaArraySyntaxChecker;

import java.util.Map;

import static org.eel.kitchen.jsonschema.util.NodeType.*;

/**
 * Utility class for builtin syntax checkers
 *
 * <p>As for other similar classes, it provides methods to retrieve checkers
 * defined by draft v3 and draft v4.</p>
 */

public final class SyntaxCheckers
{
    private static final Map<String, SyntaxChecker> DRAFTV3;
    private static final Map<String, SyntaxChecker> DRAFTV4;

    // No making new instances of this class
    private SyntaxCheckers()
    {
    }

    static {
        final MapBuilder<SyntaxChecker> common = MapBuilder.create();

        String keyword;
        SyntaxChecker checker;

        /*
         * Common syntax checkers
         */

        // Arrays
        keyword = "additionalItems";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN, OBJECT);
        common.put(keyword, checker);

        keyword = "minItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "maxItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "uniqueItems";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        common.put(keyword, checker);

        // Integer/number
        keyword = "minimum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        common.put(keyword, checker);

        keyword = "exclusiveMinimum";
        checker = ExclusiveMinimumSyntaxChecker.getInstance();
        common.put(keyword, checker);

        keyword = "maximum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        common.put(keyword, checker);

        keyword = "exclusiveMaximum";
        checker = ExclusiveMaximumSyntaxChecker.getInstance();
        common.put(keyword, checker);

        // Object
        keyword = "additionalProperties";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN, OBJECT);
        common.put(keyword, checker);

        keyword = "patternProperties";
        checker = PatternPropertiesSyntaxChecker.getInstance();
        common.put(keyword, checker);

        // String
        keyword = "minLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "maxLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "pattern";
        checker = PatternSyntaxChecker.getInstance();
        common.put(keyword, checker);

        // All/none
        keyword = "$schema";
        checker = new URISyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "$ref";
        checker = new URISyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "id";
        checker = new URISyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "description";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        common.put(keyword, checker);

        keyword = "title";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        common.put(keyword, checker);

        keyword = "enum";
        checker = EnumSyntaxChecker.getInstance();
        common.put(keyword, checker);

        keyword = "format";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        common.put(keyword, checker);

        // Build the map
        final Map<String, SyntaxChecker> commonCheckers = common.build();

        /*
         * Draft v3 specific syntax checkers
         */
        final MapBuilder<SyntaxChecker> draftv3 = MapBuilder.create();

        // Array
        keyword = "items";
        checker = DraftV3ItemsSyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        // Integer/number
        keyword = "divisibleBy";
        checker = new DivisorSyntaxChecker(keyword);
        draftv3.put(keyword, checker);

        // Object
        keyword = "properties";
        checker = DraftV3PropertiesSyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        keyword = "dependencies";
        checker = DraftV3DependenciesSyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        // All/none
        keyword = "extends";
        checker = ExtendsSyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        keyword = "type";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        draftv3.put(keyword, checker);

        keyword = "disallow";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        draftv3.put(keyword, checker);


        // Build the map: all checkers in common, plus draft v3 specific
        // checkers
        draftv3.putAll(commonCheckers);
        DRAFTV3 = draftv3.build();

        /*
         * Draft v4 specific syntax checkers
         */
        final MapBuilder<SyntaxChecker> draftv4 = MapBuilder.create();

        // Array
        keyword = "items";
        checker = DraftV4ItemsSyntaxChecker.getInstance();
        draftv4.put(keyword, checker);

        // Integer/number
        keyword = "multipleOf";
        checker = new DivisorSyntaxChecker(keyword);
        draftv4.put(keyword, checker);

        // Object
        keyword = "minProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        draftv4.put(keyword, checker);

        keyword = "maxProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        draftv4.put(keyword, checker);

        keyword = "required";
        checker = RequiredSyntaxChecker.getInstance();
        draftv4.put(keyword, checker);

        keyword = "dependencies";
        checker = DraftV4DependenciesSyntaxChecker.getInstance();
        draftv4.put(keyword, checker);

        // All/none
        keyword = "anyOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        draftv4.put(keyword, checker);

        keyword = "allOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        draftv4.put(keyword, checker);

        keyword = "oneOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        draftv4.put(keyword, checker);

        keyword = "not";
        checker = new TypeOnlySyntaxChecker(keyword, OBJECT);
        draftv4.put(keyword, checker);

        keyword = "type";
        checker = DraftV4TypeSyntaxChecker.getInstance();
        draftv4.put(keyword, checker);

        // Build the map: all checkers in common, plus draft v4 specific
        // checkers
        draftv4.putAll(commonCheckers);
        DRAFTV4 = draftv4.build();
    }

    /**
     * Return an immutable map of syntax checkers for draft v3
     *
     * @return a map pairing keyword names and their syntax checkers
     */
    public static Map<String, SyntaxChecker> draftV3()
    {
        return DRAFTV3;
    }

    /**
     * Return an immutable map of syntax checkers for draft v4
     *
     * @return a map pairing keyword names and their syntax checkers
     */
    public static Map<String, SyntaxChecker> draftV4()
    {
        return DRAFTV4;
    }
}
