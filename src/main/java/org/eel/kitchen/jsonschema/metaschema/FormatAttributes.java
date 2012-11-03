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

import org.eel.kitchen.jsonschema.format.DateTimeFormatAttribute;
import org.eel.kitchen.jsonschema.format.EmailFormatAttribute;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.format.HostnameFormatAttribute;
import org.eel.kitchen.jsonschema.format.IPV4FormatAttribute;
import org.eel.kitchen.jsonschema.format.IPV6FormatAttribute;
import org.eel.kitchen.jsonschema.format.RegexFormatAttribute;
import org.eel.kitchen.jsonschema.format.URIFormatAttribute;

import java.util.Map;

public final class FormatAttributes
{
    private static final Map<String, FormatAttribute> DRAFTV3;

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
        // TODO

        // Build the map
        draftV3.putAll(commonMap);
        DRAFTV3 = draftV3.build();
    }

    public static Map<String, FormatAttribute> draftV3()
    {
        return DRAFTV3;
    }
}
