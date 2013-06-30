package com.github.fge.jsonschema.format.extra;

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.helpers.HexStringFormatAttribute;

/**
 * Format specifier for {@code sha256}
 *
 * @see HexStringFormatAttribute
 */
public final class SHA256FormatAttribute
    extends HexStringFormatAttribute
{
    private static final FormatAttribute instance = new SHA256FormatAttribute();

    private SHA256FormatAttribute()
    {
        super("sha256", 64);
    }

    public static FormatAttribute getInstance()
    {
        return instance;
    }

}
