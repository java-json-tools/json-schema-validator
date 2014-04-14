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
import com.github.fge.jsonschema.format.common.DateTimeAttribute;
import com.github.fge.jsonschema.format.common.EmailAttribute;
import com.github.fge.jsonschema.format.common.IPv6Attribute;
import com.github.fge.jsonschema.format.common.RegexAttribute;
import com.github.fge.jsonschema.format.common.URIAttribute;

/**
 * Format attributes common to draft v4 and v3
 */
public final class CommonFormatAttributesDictionary
{
    private static final Dictionary<FormatAttribute> DICTIONARY;

    private CommonFormatAttributesDictionary()
    {
    }

    static {
        final DictionaryBuilder<FormatAttribute> builder
            = Dictionary.newBuilder();

        builder.addAll(ExtraFormatsDictionary.get());

        String name;
        FormatAttribute attribute;

        name = "date-time";
        attribute = DateTimeAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "email";
        attribute = EmailAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "ipv6";
        attribute = IPv6Attribute.getInstance();
        builder.addEntry(name, attribute);

        name = "regex";
        attribute = RegexAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "uri";
        attribute = URIAttribute.getInstance();
        builder.addEntry(name, attribute);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<FormatAttribute> get()
    {
        return DICTIONARY;
    }
}
