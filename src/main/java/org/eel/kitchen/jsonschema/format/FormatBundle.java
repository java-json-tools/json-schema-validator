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

package org.eel.kitchen.jsonschema.format;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.eel.kitchen.jsonschema.util.RhinoHelper;

import java.util.Map;

/**
 * A format specifiers bundle
 *
 * <p>You can extend the list of specifiers by creating a new bundle (using the
 * {@link #newBundle()} static factory method) and adding/removing specifiers.
 * </p>
 *
 * <p>Any newly created bundle will contain the following specifiers by default:
 * </p>
 *
 * <ul>
 *     <li>{@code date},</li>
 *     <li>{@code date-time},</li>
 *     <li>{@code date-time-ms},</li>
 *     <li>{@code email},</li>
 *     <li>{@code host-name},</li>
 *     <li>{@code ip-address},</li>
 *     <li>{@code ipv6},</li>
 *     <li>{@code phone},</li>
 *     <li>{@code regex},</li>
 *     <li>{@code time},</li>
 *     <li>{@code uri},</li>
 *     <li>{@code utc-millisec}.</li>
 * </ul>
 *
 * <p>All of these specifiers (except for {@code date-time-ms}) are defined
 * by the draft. The only missing specifiers are {@code color} and {@code
 * style}. See the draft for more details.</p>
 *
 * <p>you can override the bundled specifiers, but it is <b>not</b> advised that
 * you do so: they have been carefully designed so as to conform to the
 * specification as closely as possible -- in particular, leave {@code regex}
 * alone (see {@link RhinoHelper}).</p>
 *
 * @see FormatSpecifier
 */
public final class FormatBundle
{
    /**
     * The builtin format specifiers
     */
    private static final Map<String, FormatSpecifier> BUILTIN_FORMATS;

    static {
        final ImmutableMap.Builder<String, FormatSpecifier> builder
            = new ImmutableMap.Builder<String, FormatSpecifier>();

        /*
         * Draft v3 format specifiers
         */
        builder.put("date", DateFormatSpecifier.getInstance());
        builder.put("date-time", DateTimeFormatSpecifier.getInstance());
        builder.put("email", EmailFormatSpecifier.getInstance());
        builder.put("host-name", HostnameFormatSpecifier.getInstance());
        builder.put("ip-address", IPV4FormatSpecifier.getInstance());
        builder.put("ipv6", IPV6FormatSpecifier.getInstance());
        builder.put("phone", PhoneNumberFormatSpecifier.getInstance());
        builder.put("regex", RegexFormatSpecifier.getInstance());
        builder.put("time", TimeFormatSpecifier.getInstance());
        builder.put("uri", URIFormatSpecifier.getInstance());
        builder.put("utc-millisec", UnixEpochFormatSpecifier.getInstance());

        /*
         * Custom format specifiers
         */
        builder.put("date-time-ms",
            DateTimeMillisecFormatSpecifier.getInstance());

        BUILTIN_FORMATS = builder.build();
    }

    /**
     * This bundle's specifiers
     */
    private final Map<String, FormatSpecifier> specifiers;

    /**
     * Constructor, private by design
     *
     * @param specifiers the specifiers list
     */
    private FormatBundle(final Map<String, FormatSpecifier> specifiers)
    {
        this.specifiers = Maps.newHashMap(specifiers);
    }

    /**
     * Create a new bundle
     *
     * @return the bundle
     */
    public static FormatBundle newBundle()
    {
        return new FormatBundle(BUILTIN_FORMATS);
    }

    /**
     * Register a new format specifier
     *
     * <p>Note: it will unconditionally override any existing specifier, so be
     * careful!</p>
     *
     * @param fmt the format specifier name
     * @param specifier the specifier instance
     */
    public void registerFormat(final String fmt,
        final FormatSpecifier specifier)
    {
        specifiers.put(fmt, specifier);
    }

    /**
     * Unregister a specifier
     *
     * @param fmt the specifier name
     */
    public void unregisterFormat(final String fmt)
    {
        specifiers.remove(fmt);
    }

    /**
     * Get an immutable map of this bundle's specifiers
     *
     * @return a map of this bundle instance's specifiers
     */
    public Map<String, FormatSpecifier> getSpecifiers()
    {
        return ImmutableMap.copyOf(specifiers);
    }

    @Override
    public String toString()
    {
        return "specifiers: " + specifiers.keySet();
    }
}
