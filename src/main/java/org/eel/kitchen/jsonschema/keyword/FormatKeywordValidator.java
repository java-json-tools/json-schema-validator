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
import com.google.common.collect.ImmutableMap;
import org.eel.kitchen.jsonschema.ValidationContext;
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
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for the {@code format} keyword
 *
 * <p>This keyword is scheduled for disappearance in draft v4. However,
 * some people have raised concerns about this.</p>
 *
 * <p>All format specifiers fom draft v3 are supported except {@code style}
 * and {@code color} (which validate an entire CSS 2.1 style and color
 * respectively!).</p>
 *
 * @see org.eel.kitchen.jsonschema.format
 */
public final class FormatKeywordValidator
    extends KeywordValidator
{
    /**
     * Static final map of all format specifiers. We choose to not allow to
     * add new specifiers, even though it is theoretically possible (MAY in
     * the draft).
     */
    private static final Map<String, FormatSpecifier> specifiers;

    static {
        final ImmutableMap.Builder<String, FormatSpecifier> builder
            = new ImmutableMap.Builder<String, FormatSpecifier>();

        builder.put("date-time", DateTimeFormatSpecifier.getInstance());
        builder.put("date", DateFormatSpecifier.getInstance());
        builder.put("time", TimeFormatSpecifier.getInstance());
        builder.put("utc-millisec", UnixEpochFormatSpecifier.getInstance());
        builder.put("regex", RegexFormatSpecifier.getInstance());
        builder.put("phone", PhoneNumberFormatSpecifier.getInstance());
        builder.put("uri", URIFormatSpecifier.getInstance());
        builder.put("email", EmailFormatSpecifier.getInstance());
        builder.put("ip-address", IPV4FormatSpecifier.getInstance());
        builder.put("ipv6", IPV6FormatSpecifier.getInstance());
        builder.put("host-name", HostnameFormatSpecifier.getInstance());

        // Here is one special specifier for date-time with milliseconds
        builder.put("date-time-ms", DateTimeMillisecFormatSpecifier.getInstance());

        specifiers = builder.build();
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
        if (specifier == null)
            return;

        final List<String> messages = new ArrayList<String>();
        specifier.validate(messages, instance);
        context.addMessages(messages);
    }
}
