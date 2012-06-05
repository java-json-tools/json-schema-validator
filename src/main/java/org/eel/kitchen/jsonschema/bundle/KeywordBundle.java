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

package org.eel.kitchen.jsonschema.bundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class KeywordBundle
    implements Iterable<Map.Entry<String, Keyword>>
{
    private final Map<String, Keyword> keywords
        = new HashMap<String, Keyword>();

    KeywordBundle copy()
    {
        final KeywordBundle ret = new KeywordBundle();
        ret.keywords.putAll(keywords);
        return ret;
    }

    public void registerKeyword(final Keyword keyword)
    {
        final String name = keyword.getName();
        if (keywords.containsKey(name))
            throw new IllegalArgumentException("keyword \"" + name + "\" "
                + "already registered");
        keywords.put(name, keyword);
    }

    public void unregisterKeyword(final String name)
    {
        keywords.remove(name);
    }

    public Map<String, Keyword> getKeywords()
    {
        return Collections.unmodifiableMap(keywords);
    }

    @Override
    public Iterator<Map.Entry<String, Keyword>> iterator()
    {
        return Collections.unmodifiableMap(keywords).entrySet().iterator();
    }
}
