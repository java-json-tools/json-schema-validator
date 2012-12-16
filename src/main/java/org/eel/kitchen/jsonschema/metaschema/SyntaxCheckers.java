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

import com.google.common.collect.ImmutableMap;
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
import org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3.ContentEncodingSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3.FragmentResolutionSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3.LinksSyntaxChecker;
import org.eel.kitchen.jsonschema.syntax.hyperschema.draftv3.MediaTypeSyntaxChecker;

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
    private static final Map<String, SyntaxChecker> DRAFTV3_HYPERSCHEMA;
    private static final Map<String, SyntaxChecker> DRAFTV4;

    // No making new instances of this class
    private SyntaxCheckers()
    {
    }

    static {
        ImmutableMap.Builder<String, SyntaxChecker> builder;

        String keyword;
        SyntaxChecker checker;

        /*
         * Common syntax checkers
         */
        builder = ImmutableMap.builder();

        // Arrays
        keyword = "additionalItems";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN, OBJECT);
        builder.put(keyword, checker);

        keyword = "minItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "maxItems";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "uniqueItems";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        builder.put(keyword, checker);

        // Integer/number
        keyword = "minimum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        builder.put(keyword, checker);

        keyword = "exclusiveMinimum";
        checker = ExclusiveMinimumSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "maximum";
        checker = new TypeOnlySyntaxChecker(keyword, INTEGER, NUMBER);
        builder.put(keyword, checker);

        keyword = "exclusiveMaximum";
        checker = ExclusiveMaximumSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // Object
        keyword = "additionalProperties";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN, OBJECT);
        builder.put(keyword, checker);

        keyword = "patternProperties";
        checker = PatternPropertiesSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // String
        keyword = "minLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "maxLength";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "pattern";
        checker = PatternSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // All/none
        keyword = "$schema";
        checker = new URISyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "$ref";
        checker = new URISyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "id";
        checker = new URISyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "description";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        builder.put(keyword, checker);

        keyword = "title";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        builder.put(keyword, checker);

        keyword = "enum";
        checker = EnumSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "format";
        checker = new TypeOnlySyntaxChecker(keyword, STRING);
        builder.put(keyword, checker);

        // Build the map
        final Map<String, SyntaxChecker> common= builder.build();

        /*
         * Draft v3
         */
        builder = ImmutableMap.builder();

        // Start by injecting syntax checkers common to all drafts
        builder.putAll(common);

        // Now, inject draft v3 specific syntax checkers

        // Array
        keyword = "items";
        checker = DraftV3ItemsSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // Integer/number
        keyword = "divisibleBy";
        checker = new DivisorSyntaxChecker(keyword);
        builder.put(keyword, checker);

        // Object
        keyword = "properties";
        checker = DraftV3PropertiesSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "dependencies";
        checker = DraftV3DependenciesSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // All/none
        keyword = "extends";
        checker = ExtendsSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "type";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "disallow";
        checker = new DraftV3TypeKeywordSyntaxChecker(keyword);
        builder.put(keyword, checker);

        // Build the map
        DRAFTV3 = builder.build();

        /*
         * Draft v3 hyper schema
         */
        builder = ImmutableMap.builder();

        // Inject all of draft v3 core keywords
        builder.putAll(DRAFTV3);

        // Inject hyper schema specific keywords
        keyword = "fragmentResolution";
        checker = FragmentResolutionSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "readonly";
        checker = new TypeOnlySyntaxChecker(keyword, BOOLEAN);
        builder.put(keyword, checker);

        keyword = "contentEncoding";
        checker = ContentEncodingSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "pathStart";
        checker = new URISyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "mediaType";
        checker = MediaTypeSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "links";
        checker = LinksSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // Build the map
        DRAFTV3_HYPERSCHEMA = builder.build();

        /*
         * Draft v4
         */
        builder = ImmutableMap.builder();

        // Start by injecting syntax checkers common to all drafts
        builder.putAll(common);

        // Now, inject draft v4 specific syntax checkers

        // Array
        keyword = "items";
        checker = DraftV4ItemsSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // Integer/number
        keyword = "multipleOf";
        checker = new DivisorSyntaxChecker(keyword);
        builder.put(keyword, checker);

        // Object
        keyword = "minProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "maxProperties";
        checker = new PositiveIntegerSyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "required";
        checker = RequiredSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        keyword = "dependencies";
        checker = DraftV4DependenciesSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // All/none
        keyword = "anyOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "allOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "oneOf";
        checker = new SchemaArraySyntaxChecker(keyword);
        builder.put(keyword, checker);

        keyword = "not";
        checker = new TypeOnlySyntaxChecker(keyword, OBJECT);
        builder.put(keyword, checker);

        keyword = "type";
        checker = DraftV4TypeSyntaxChecker.getInstance();
        builder.put(keyword, checker);

        // Build the map
        DRAFTV4 = builder.build();
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
     * Return an immutable map of syntax checkers for draft v3 hyper schema
     *
     * @return a map pairing keyword names and their syntax checkers
     */
    public static Map<String, SyntaxChecker> draftV3HyperSchema()
    {
        return DRAFTV3_HYPERSCHEMA;
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
