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

import org.eel.kitchen.jsonschema.syntax.PositiveIntegerSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SimpleSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.common.DivisibleBySyntaxChecker;
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

import java.util.Map;

import static org.eel.kitchen.jsonschema.util.NodeType.*;

public final class SyntaxCheckers
{
    private static final Map<String, SyntaxChecker> DRAFTV3;

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
        checker = new SimpleSyntaxChecker(keyword, BOOLEAN, OBJECT);
        common.put(keyword, checker);

        keyword = "minItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "maxItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "uniqueItems";
        checker = new SimpleSyntaxChecker(keyword, BOOLEAN);
        common.put(keyword, checker);

        // Integer/number
        keyword = "minimum";
        checker = new SimpleSyntaxChecker(keyword, INTEGER, NUMBER);
        common.put(keyword, checker);

        keyword = "exclusiveMinimum";
        checker = ExclusiveMinimumSyntaxChecker.getInstance();
        common.put(keyword, checker);

        keyword = "maximum";
        checker = new SimpleSyntaxChecker(keyword, INTEGER, NUMBER);
        common.put(keyword, checker);

        keyword = "exclusiveMaximum";
        checker = ExclusiveMaximumSyntaxChecker.getInstance();
        common.put(keyword, checker);

        // Object
        keyword = "additionalProperties";
        checker = new SimpleSyntaxChecker(keyword, BOOLEAN, OBJECT);
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
        checker = new SimpleSyntaxChecker(keyword, STRING);
        common.put(keyword, checker);

        keyword = "title";
        checker = new SimpleSyntaxChecker(keyword, STRING);
        common.put(keyword, checker);

        keyword = "enum";
        checker = EnumSyntaxChecker.getInstance();
        common.put(keyword, checker);

        keyword = "format";
        checker = new SimpleSyntaxChecker(keyword, STRING);
        common.put(keyword, checker);

        keyword = "type";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        common.put(keyword, checker);

        keyword = "disallow";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
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
        checker = DivisibleBySyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        // Object
        keyword = "properties";
        checker = DraftV3PropertiesSyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        keyword = "dependencies";
        checker = DraftV3DependenciesSyntaxChecker.getInstance();
        draftv3.put(keyword, checker);

        // Build the map: all checkers in common, plus draft v3 specific
        // checkers
        draftv3.putAll(commonCheckers);
        DRAFTV3 = draftv3.build();
    }

    public static Map<String, SyntaxChecker> draftV3()
    {
        return DRAFTV3;
    }

    public static Map<String, SyntaxChecker> defaultCheckers()
    {
        return draftV3();
    }
}
