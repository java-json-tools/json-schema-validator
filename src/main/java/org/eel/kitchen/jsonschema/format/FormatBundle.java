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

import java.util.Collections;
import java.util.Map;

/**
 * A format specifiers bundle
 *
 * <p>You can either create a bundle with all builtin format specifiers (see
 * below for the list), or an empty one which you have to fill yourself. The
 * methods to use for these are respectively {@link #defaultBundle()} and {@link
 * #newBundle()}.</p>
 *
 * <p>The default bundle will contain validators ({@link FormatSpecifier}
 * instances) for the following format attributes:</p>
 *
 * <ul>
 *     <li>{@code date-time},</li>
 *     <li>{@code email},</li>
 *     <li>{@code host-name},</li>
 *     <li>{@code ip-address},</li>
 *     <li>{@code ipv6},</li>
 *     <li>{@code regex},</li>
 *     <li>{@code uri},</li>
 * </ul>
 *
 * <p>Other format attributes defined by the specification are hosted in a
 * different package: <a href="https://github.com/fge/json-schema-formats">
 * json-schema-formats</a>.</p>
 *
 * <p>you can override these, but it is <b>not</b> advised that you do so: they
 * have been carefully designed so as to conform to the specification as closely
 * as possible -- in particular, leave {@code regex} alone (see
 * {@link RhinoHelper}).</p>
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
        builder.put("date-time", DateTimeFormatSpecifier.getInstance());
        builder.put("email", EmailFormatSpecifier.getInstance());
        builder.put("host-name", HostnameFormatSpecifier.getInstance());
        builder.put("ip-address", IPV4FormatSpecifier.getInstance());
        builder.put("ipv6", IPV6FormatSpecifier.getInstance());
        builder.put("regex", RegexFormatSpecifier.getInstance());
        builder.put("uri", URIFormatSpecifier.getInstance());

        BUILTIN_FORMATS = builder.build();
    }

    /**
     * This bundle's specifiers
     */
    private final Map<String, FormatSpecifier> specifiers;

    /**
     * Constructor for a completely empty bundle
     */
    private FormatBundle()
    {
        this(Collections.<String, FormatSpecifier>emptyMap());
    }

    /**
     * Constructor with a provided specifier map
     *
     * @param specifiers the specifiers list
     */
    private FormatBundle(final Map<String, FormatSpecifier> specifiers)
    {
        this.specifiers = Maps.newHashMap(specifiers);
    }

    /**
     * Create a new, empty bundle
     *
     * @return the bundle
     */
    public static FormatBundle newBundle()
    {
        return new FormatBundle();
    }

    /**
     * Create a new bundle with the default format specifier set
     *
     * @return the bundle
     */
    public static FormatBundle defaultBundle()
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
     * Merge with another bundle
     *
     * <p>Note that specifiers defined in the bundle given as an argument will
     * happily override those defined in the current bundle. Use with care!</p>
     *
     * @param other the other bundle
     */
    public void mergeWith(final FormatBundle other)
    {
        specifiers.putAll(other.specifiers);
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
