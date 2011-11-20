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
 * GNU General Public License for more details.
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.eel.kitchen.util.NodeType.*;

public abstract class ValidatorBundle
{
    protected final Map<String, SyntaxValidator> svMap
        = new HashMap<String, SyntaxValidator>();

    protected final Set<String> ignoredSV = new HashSet<String>();

    protected final Map<NodeType, Map<String, KeywordValidator>> kvMap
        = new EnumMap<NodeType, Map<String, KeywordValidator>>(NodeType.class);

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

    public final Map<String, SyntaxValidator> syntaxValidators()
    {
        return Collections.unmodifiableMap(svMap);
    }

    public final Set<String> ignoredSyntaxValidators()
    {
        return Collections.unmodifiableSet(ignoredSV);
    }

    public final Map<NodeType, Map<String, KeywordValidator>> keywordValidators()
    {
        return Collections.unmodifiableMap(kvMap);
    }

    public final Map<NodeType, Set<String>> ignoredKeywordValidators()
    {
        return Collections.unmodifiableMap(ignoredKV);
    }

    public final void registerSV(final String keyword,
        final SyntaxValidator sv)
    {
        if (sv == null) {
            registerIgnoredSV(keyword);
            return;
        }

        svMap.put(keyword, sv);
    }

    protected void registerIgnoredSV(final String keyword)
    {
        ignoredSV.add(keyword);
    }

    public final void unregisterSV(final String keyword)
    {
        ignoredSV.remove(keyword);
        svMap.remove(keyword);
    }

    public final void registerKV(final String keyword,
        final KeywordValidator kv, final NodeType... types)
    {
        if (kv == null) {
            registerIgnoredKV(keyword, types);
            return;
        }

        for (final NodeType type: types)
            kvMap.get(type).put(keyword, kv);
    }

    protected void registerIgnoredKV(final String keyword,
        final NodeType... types)
    {
        for (final NodeType type: types)
            ignoredKV.get(type).add(keyword);
    }

    public final EnumSet<NodeType> unregisterKV(final String keyword)
    {
        final EnumSet<NodeType> ret = EnumSet.noneOf(NodeType.class);

        for (final NodeType type: values()) {
            if (ignoredKV.get(type).remove(keyword))
                ret.add(type);
            if (kvMap.get(type).remove(keyword) != null)
                ret.add(type);
        }

        return ret;
    }

    public final boolean hasKeyword(final String keyword)
    {
        if (ignoredSV.contains(keyword))
            return true;
        if (svMap.containsKey(keyword))
            return true;

        for (final NodeType type: values()) {
            if (ignoredKV.get(type).contains(keyword))
                return true;
            if (kvMap.get(type).containsKey(keyword))
                return true;
        }

        return false;
    }
}
