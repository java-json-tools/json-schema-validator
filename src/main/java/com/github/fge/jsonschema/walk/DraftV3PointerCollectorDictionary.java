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

package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.walk.helpers.DraftV3TypeKeywordPointerCollector;
import com.github.fge.jsonschema.walk.helpers.SchemaOrSchemaArrayPointerCollector;

public final class DraftV3PointerCollectorDictionary
{
    private static final Dictionary<PointerCollector> DICTIONARY;

    private DraftV3PointerCollectorDictionary()
    {
    }

    static {
        final DictionaryBuilder<PointerCollector> builder
            = Dictionary.newBuilder();

        builder.addAll(CommonPointerCollectorDictionary.get());

        String keyword;
        PointerCollector collector;

        keyword = "disallow";
        collector = new DraftV3TypeKeywordPointerCollector(keyword);
        builder.addEntry(keyword, collector);

        keyword = "extends";
        collector = new SchemaOrSchemaArrayPointerCollector(keyword);
        builder.addEntry(keyword, collector);

        keyword = "type";
        collector = new DraftV3TypeKeywordPointerCollector(keyword);
        builder.addEntry(keyword, collector);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<PointerCollector> get()
    {
        return DICTIONARY;
    }
}
