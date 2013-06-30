package com.github.fge.jsonschema.library.format;

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.extra.Base64FormatAttribute;
import com.github.fge.jsonschema.format.extra.JsonPointerFormatAttribute;
import com.github.fge.jsonschema.format.extra.MD5FormatAttribute;
import com.github.fge.jsonschema.format.extra.MacAddressFormatAttribute;
import com.github.fge.jsonschema.format.extra.SHA1FormatAttribute;
import com.github.fge.jsonschema.format.extra.SHA256FormatAttribute;
import com.github.fge.jsonschema.format.extra.SHA512FormatAttribute;
import com.github.fge.jsonschema.format.extra.UUIDFormatAttribute;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;

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
