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

package org.eel.kitchen.jsonschema.metaschema;

import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.format.common.DateTimeFormatAttribute;
import org.eel.kitchen.jsonschema.format.common.EmailFormatAttribute;
import org.eel.kitchen.jsonschema.format.common.HostnameFormatAttribute;
import org.eel.kitchen.jsonschema.format.common.IPV4FormatAttribute;
import org.eel.kitchen.jsonschema.format.common.IPV6FormatAttribute;
import org.eel.kitchen.jsonschema.format.common.RegexFormatAttribute;
import org.eel.kitchen.jsonschema.format.common.URIFormatAttribute;
import org.eel.kitchen.jsonschema.format.draftv3.DateFormatAttribute;
import org.eel.kitchen.jsonschema.format.draftv3.PhoneNumberFormatAttribute;
import org.eel.kitchen.jsonschema.format.draftv3.TimeFormatAttribute;
import org.eel.kitchen.jsonschema.format.draftv3.UnixEpochFormatAttribute;

import java.util.Map;

public final class FormatAttributes
{
    private static final Map<String, FormatAttribute> DRAFTV3;
    private static final Map<String, FormatAttribute> DRAFTV4;

    // No instantiations for this class
    private FormatAttributes()
    {
    }

    static {
        final MapBuilder<FormatAttribute> common = MapBuilder.create();

        /*
         * Common format attributes
         */
        common.put("date-time", DateTimeFormatAttribute.getInstance());
        common.put("email", EmailFormatAttribute.getInstance());
        common.put("host-name", HostnameFormatAttribute.getInstance());
        common.put("ip-address", IPV4FormatAttribute.getInstance());
        common.put("ipv6", IPV6FormatAttribute.getInstance());
        common.put("regex", RegexFormatAttribute.getInstance());
        common.put("uri", URIFormatAttribute.getInstance());

        // Build the map
        final Map<String, FormatAttribute> commonMap = common.build();

        /*
         * Draft v3 specific format attributes
         */
        final MapBuilder<FormatAttribute> draftV3 = MapBuilder.create();

        draftV3.put("date", DateFormatAttribute.getInstance());
        draftV3.put("phone", PhoneNumberFormatAttribute.getInstance());
        draftV3.put("time", TimeFormatAttribute.getInstance());
        draftV3.put("utc-millisec", UnixEpochFormatAttribute.getInstance());

        // Build the map
        draftV3.putAll(commonMap);
        DRAFTV3 = draftV3.build();

        /*
         * Draft v4 specific format attributes
         */
        final MapBuilder<FormatAttribute> draftV4 = MapBuilder.create();

        // None

        // Build the map
        draftV4.putAll(commonMap);
        DRAFTV4 = draftV4.build();
    }

    public static Map<String, FormatAttribute> draftV3()
    {
        return DRAFTV3;
    }

    public static Map<String, FormatAttribute> draftV4()
    {
        return DRAFTV4;
    }
}
