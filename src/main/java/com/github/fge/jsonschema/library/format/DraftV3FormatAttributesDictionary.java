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

package com.github.fge.jsonschema.library.format;

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.UTCMillisecAttribute;
import com.github.fge.jsonschema.format.draftv3.DateAttribute;
import com.github.fge.jsonschema.format.draftv3.PhoneAttribute;
import com.github.fge.jsonschema.format.draftv3.TimeAttribute;
import com.github.fge.jsonschema.format.helpers.IPv4FormatAttribute;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

public final class DraftV3FormatAttributesDictionary
{
    private static final Dictionary<FormatAttribute> DICTIONARY;

    private DraftV3FormatAttributesDictionary()
    {
    }

    static {
        final DictionaryBuilder<FormatAttribute> builder
            = Dictionary.newBuilder();

        builder.addAll(CommonFormatAttributesDictionary.get());

        String name;
        FormatAttribute attribute;

        name = "date";
        attribute = DateAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "ip-address";
        attribute = new IPv4FormatAttribute(name);
        builder.addEntry(name, attribute);

        name = "phone";
        attribute = PhoneAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "time";
        attribute = TimeAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "utc-millisec";
        attribute = UTCMillisecAttribute.getInstance();
        builder.addEntry(name, attribute);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<FormatAttribute> get()
    {
        return DICTIONARY;
    }
}
