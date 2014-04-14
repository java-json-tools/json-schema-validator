/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.library.format;

import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.draftv3.DateAttribute;
import com.github.fge.jsonschema.format.draftv3.PhoneAttribute;
import com.github.fge.jsonschema.format.draftv3.TimeAttribute;
import com.github.fge.jsonschema.format.draftv3.UTCMillisecAttribute;
import com.github.fge.jsonschema.format.helpers.IPv4FormatAttribute;
import com.github.fge.jsonschema.format.helpers.SharedHostNameAttribute;

/**
 * Draft v3 specific format attributes
 */
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

        name = "host-name";
        attribute = new SharedHostNameAttribute("host-name");
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
