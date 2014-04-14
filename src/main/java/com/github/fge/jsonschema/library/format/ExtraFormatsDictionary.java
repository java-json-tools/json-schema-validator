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
import com.github.fge.jsonschema.format.extra.Base64FormatAttribute;
import com.github.fge.jsonschema.format.extra.JsonPointerFormatAttribute;
import com.github.fge.jsonschema.format.extra.MD5FormatAttribute;
import com.github.fge.jsonschema.format.extra.MacAddressFormatAttribute;
import com.github.fge.jsonschema.format.extra.SHA1FormatAttribute;
import com.github.fge.jsonschema.format.extra.SHA256FormatAttribute;
import com.github.fge.jsonschema.format.extra.SHA512FormatAttribute;
import com.github.fge.jsonschema.format.extra.UUIDFormatAttribute;

public final class ExtraFormatsDictionary
{
    private static final Dictionary<FormatAttribute> DICTIONARY;

    private ExtraFormatsDictionary()
    {
    }

    static {
        final DictionaryBuilder<FormatAttribute> builder
            = Dictionary.newBuilder();

        String name;
        FormatAttribute attribute;

        name = "base64";
        attribute = Base64FormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "json-pointer";
        attribute = JsonPointerFormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "mac";
        attribute = MacAddressFormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "md5";
        attribute = MD5FormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "sha1";
        attribute = SHA1FormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "sha256";
        attribute = SHA256FormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "sha512";
        attribute = SHA512FormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        name = "uuid";
        attribute = UUIDFormatAttribute.getInstance();
        builder.addEntry(name, attribute);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<FormatAttribute> get()
    {
        return DICTIONARY;
    }
}
