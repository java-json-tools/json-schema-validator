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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.format.DateFormatSpecifier;
import org.eel.kitchen.jsonschema.format.DateTimeFormatSpecifier;
import org.eel.kitchen.jsonschema.format.EmailFormatSpecifier;
import org.eel.kitchen.jsonschema.format.FormatSpecifier;
import org.eel.kitchen.jsonschema.format.HostnameFormatSpecifier;
import org.eel.kitchen.jsonschema.format.IPV4FormatSpecifier;
import org.eel.kitchen.jsonschema.format.IPV6FormatSpecifier;
import org.eel.kitchen.jsonschema.format.PhoneNumberFormatSpecifier;
import org.eel.kitchen.jsonschema.format.RegexFormatSpecifier;
import org.eel.kitchen.jsonschema.format.TimeFormatSpecifier;
import org.eel.kitchen.jsonschema.format.URIFormatSpecifier;
import org.eel.kitchen.jsonschema.format.UnixEpochFormatSpecifier;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.util.NodeType;

import java.util.HashMap;
import java.util.Map;

/**
 * Validator for the {@code format} keyword
 *
 * <p>This keyword is scheduled for disappearance in draft v4. However,
 * some people have raised concerns about this.</p>
 *
 * @see org.eel.kitchen.jsonschema.format
 */
public final class FormatKeywordValidator
    extends KeywordValidator
{
    private static final Map<String, FormatSpecifier> specifiers
        = new HashMap<String, FormatSpecifier>();

    static {
        specifiers.put("date-time", DateTimeFormatSpecifier.getInstance());
        specifiers.put("date", DateFormatSpecifier.getInstance());
        specifiers.put("time", TimeFormatSpecifier.getInstance());
        specifiers.put("utc-millisec", UnixEpochFormatSpecifier.getInstance());
        specifiers.put("regex", RegexFormatSpecifier.getInstance());
        specifiers.put("phone", PhoneNumberFormatSpecifier.getInstance());
        specifiers.put("uri", URIFormatSpecifier.getInstance());
        specifiers.put("email", EmailFormatSpecifier.getInstance());
        specifiers.put("ip-address", IPV4FormatSpecifier.getInstance());
        specifiers.put("ipv6", IPV6FormatSpecifier.getInstance());
        specifiers.put("host-name", HostnameFormatSpecifier.getInstance());
    }

    private final FormatSpecifier specifier;

    public FormatKeywordValidator(final JsonNode schema)
    {
        super(NodeType.values());
        specifier = specifiers.get(schema.get("format").textValue());
    }

    @Override
    protected void validate(final ValidationContext context,
        final JsonNode instance)
    {
        if (specifier != null)
            specifier.validate(context, instance);
    }
}
