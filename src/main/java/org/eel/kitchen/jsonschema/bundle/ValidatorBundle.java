/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.bundle;

import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.eel.kitchen.util.NodeType.*;

/**
 * A validator bundle
 *
 * <p>As validators vary from one schema to another (and as you can even
 * register your own validators), this class is here to relieve factories
 * from registering validators themselves.</p>
 */
public abstract class ValidatorBundle
{
    /**
     * The {@link SyntaxValidator} map
     */
    protected final Map<String, SyntaxValidator> svMap
        = new HashMap<String, SyntaxValidator>();

    /**
     * Keywords to ignore for syntax validation
     */
    protected final Set<String> ignoredSV = new HashSet<String>();

    /**
     * The {@link KeywordValidator} map
     */
    protected final Map<NodeType, Map<String, KeywordValidator>> kvMap
        = new EnumMap<NodeType, Map<String, KeywordValidator>>(NodeType.class);

    /**
     * Keywords to ignore for instance validation
     */
    protected final Map<NodeType, Set<String>> ignoredKV
        = new EnumMap<NodeType, Set<String>>(NodeType.class);

    protected ValidatorBundle()
    {
        /*
         * Initialize keyword validator maps
         */
        for (final NodeType type: values()) {
            kvMap.put(type, new HashMap<String, KeywordValidator>());
            ignoredKV.put(type, new HashSet<String>());
        }
    }

    /**
     * Return the list of registered syntax validators
     *
     * @return a map pairing keywords to their validators
     */
    public final Map<String, SyntaxValidator> syntaxValidators()
    {
        return Collections.unmodifiableMap(svMap);
    }

    /**
     * Return the set of ignored keywords on syntax validation
     *
     * @return the set
     */
    public final Set<String> ignoredSyntaxValidators()
    {
        return Collections.unmodifiableSet(ignoredSV);
    }

    /**
     * Return the list of registered keyword validators and associated
     * instance types
     *
     * @return a map pairing instance types and keywords to validators
     */
    public final Map<NodeType, Map<String, KeywordValidator>> keywordValidators()
    {
        return Collections.unmodifiableMap(kvMap);
    }

    /**
     * Return the list of ignored keywords for instance validation
     *
     * @return a map pairing the instance types and set of keywords
     */
    public final Map<NodeType, Set<String>> ignoredKeywordValidators()
    {
        return Collections.unmodifiableMap(ignoredKV);
    }

    /**
     * Register a syntax validator
     *
     * @param keyword the keyword
     * @param sv the syntax validator
     */
    protected final void registerSV(final String keyword,
        final SyntaxValidator sv)
    {
        svMap.put(keyword, sv);
    }

    /**
     * Register an ignored keyword for syntax validation
     *
     * @param keyword the keyword
     */
    protected void registerIgnoredSV(final String keyword)
    {
        ignoredSV.add(keyword);
    }

    /**
     * Register a keyword validator for a given keyword and a set of types
     *
     * @param keyword the keyword
     * @param kv the validator
     * @param types the list of types
     * @throws IllegalArgumentException the list of types is empty
     */
    protected final void registerKV(final String keyword,
        final KeywordValidator kv, final NodeType... types)
    {
        if (types.length == 0)
            throw new IllegalArgumentException("cannot register a keyword "
                + "validator with no JSON types to validate against");

        for (final NodeType type: types)
            kvMap.get(type).put(keyword, kv);
    }

    /**
     * Register an ignored keyword for keyword validations
     *
     * @param keyword the keyword
     * @param types the associated node types
     */
    protected final void registerIgnoredKV(final String keyword,
        final NodeType... types)
    {
        for (final NodeType type: types)
            ignoredKV.get(type).add(keyword);
    }

    public final void validate()
    {
        final Set<String> s = new HashSet<String>();
        final Set<String> k = new HashSet<String>();

        s.addAll(ignoredSV);
        s.addAll(svMap.keySet());

        for (final Set<String> set: ignoredKV.values())
            k.addAll(set);

        for (final Map<String, KeywordValidator> map: kvMap.values())
            k.addAll(map.keySet());

        if (!s.equals(k))
            throw new IllegalArgumentException("registered syntax/keyword "
                + "validators do not cover the same set of keywords");
    }
}
