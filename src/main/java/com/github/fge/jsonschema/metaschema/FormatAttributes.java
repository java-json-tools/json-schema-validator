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

package com.github.fge.jsonschema.metaschema;

import com.google.common.collect.ImmutableMap;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.format.IPV4FormatAttribute;
import com.github.fge.jsonschema.format.common.DateTimeFormatAttribute;
import com.github.fge.jsonschema.format.common.EmailFormatAttribute;
import com.github.fge.jsonschema.format.common.HostnameFormatAttribute;
import com.github.fge.jsonschema.format.common.IPV6FormatAttribute;
import com.github.fge.jsonschema.format.common.RegexFormatAttribute;
import com.github.fge.jsonschema.format.common.URIFormatAttribute;
import com.github.fge.jsonschema.format.draftv3.DateFormatAttribute;
import com.github.fge.jsonschema.format.draftv3.MediaTypeFormatAttribute;
import com.github.fge.jsonschema.format.draftv3.PhoneNumberFormatAttribute;
import com.github.fge.jsonschema.format.draftv3.TimeFormatAttribute;
import com.github.fge.jsonschema.format.draftv3.UnixEpochFormatAttribute;

import java.util.Map;

/**
 * Utility class for builtin format attributes
 *
 * <p>Both attributes for draft v3 and draft v4 are bundled.</p>
 *
 * <p>You should not have to use this class directly: use {@link MetaSchema}
 * instead.</p>
 */
// TODO: make package private in next version
public final class FormatAttributes
{
    private static final Map<String, FormatAttribute> DRAFTV3;
    private static final Map<String, FormatAttribute> DRAFTV4;

    // No instantiations for this class
    private FormatAttributes()
    {
    }

    static {
        ImmutableMap.Builder<String, FormatAttribute> builder;

        /*
         * Common format attributes
         */
        builder = ImmutableMap.builder();

        builder.put("date-time", DateTimeFormatAttribute.getInstance());
        builder.put("email", EmailFormatAttribute.getInstance());
        builder.put("host-name", HostnameFormatAttribute.getInstance());
        builder.put("ipv6", IPV6FormatAttribute.getInstance());
        builder.put("regex", RegexFormatAttribute.getInstance());
        builder.put("uri", URIFormatAttribute.getInstance());

        // Build the map
        final Map<String, FormatAttribute> common = builder.build();

        /*
         * Draft v3 specific format attributes
         */
        builder = ImmutableMap.builder();

        // Inject common format attributes
        builder.putAll(common);

        // Inject draft v3 specific format attributes
        builder.put("date", DateFormatAttribute.getInstance());
        builder.put("ip-address", IPV4FormatAttribute.getInstance());
        builder.put("phone", PhoneNumberFormatAttribute.getInstance());
        builder.put("time", TimeFormatAttribute.getInstance());
        builder.put("utc-millisec", UnixEpochFormatAttribute.getInstance());
        builder.put("media-type", MediaTypeFormatAttribute.getInstance());

        // Build the map
        DRAFTV3 = builder.build();

        /*
         * Draft v4 specific format attributes
         */
        builder = ImmutableMap.builder();

        // Inject common format attributes
        builder.putAll(common);

        // Inject draft v4 specific format attributes
        builder.put("ipv4", IPV4FormatAttribute.getInstance());

        // Build the map
        DRAFTV4 = builder.build();
    }

    /**
     * Immutable map of defined format attributes for draft v3
     *
     * @return a map pairing format attribute names and their implementation
     */
    static Map<String, FormatAttribute> draftV3()
    {
        return DRAFTV3;
    }

    /**
     * Immutable map of defined format attributes for draft v4
     *
     * @return a map pairing format attribute names and their implementation
     */
    static Map<String, FormatAttribute> draftV4()
    {
        return DRAFTV4;
    }
}
