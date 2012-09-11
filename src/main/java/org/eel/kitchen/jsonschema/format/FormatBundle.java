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
 * A bundle of format attributes
 *
 * <p>You can either create a bundle with all builtin format attributes (see
 * below for the list), or an empty one which you have to fill yourself. The
 * methods to use for these are respectively {@link #defaultBundle()} and {@link
 * #newBundle()}.</p>
 *
 * <p>The default bundle will contain validators ({@link FormatAttribute}
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
 * @see FormatAttribute
 */
public final class FormatBundle
{
    /**
     * The builtin format attributes
     */
    private static final Map<String, FormatAttribute> BUILTIN_FORMATS;

    static {
        final ImmutableMap.Builder<String, FormatAttribute> builder
            = new ImmutableMap.Builder<String, FormatAttribute>();

        /*
         * Draft v3 format attributes
         */
        builder.put("date-time", DateTimeFormatAttribute.getInstance());
        builder.put("email", EmailFormatAttribute.getInstance());
        builder.put("host-name", HostnameFormatAttribute.getInstance());
        builder.put("ip-address", IPV4FormatAttribute.getInstance());
        builder.put("ipv6", IPV6FormatAttribute.getInstance());
        builder.put("regex", RegexFormatAttribute.getInstance());
        builder.put("uri", URIFormatAttribute.getInstance());

        BUILTIN_FORMATS = builder.build();
    }

    /**
     * This bundle's attributes
     */
    private final Map<String, FormatAttribute> attributes;

    /**
     * Constructor for a completely empty bundle
     */
    private FormatBundle()
    {
        this(Collections.<String, FormatAttribute>emptyMap());
    }

    /**
     * Constructor with a provided attribute map
     *
     * @param attributes the attributes list
     */
    private FormatBundle(final Map<String, FormatAttribute> attributes)
    {
        this.attributes = Maps.newHashMap(attributes);
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
     * Create a new bundle with the default format attribute set
     *
     * @return the bundle
     */
    public static FormatBundle defaultBundle()
    {
        return new FormatBundle(BUILTIN_FORMATS);
    }

    /**
     * Register a new format attribute
     *
     * <p>Note: it will unconditionally override any existing attribute, so be
     * careful!</p>
     *
     * @param fmt the format attribute name
     * @param attribute the attribute instance
     */
    public void registerFormat(final String fmt,
        final FormatAttribute attribute)
    {
        attributes.put(fmt, attribute);
    }

    /**
     * Unregister a attribute
     *
     * @param fmt the attribute name
     */
    public void unregisterFormat(final String fmt)
    {
        attributes.remove(fmt);
    }

    /**
     * Merge with another bundle
     *
     * <p>Note that attributes defined in the bundle given as an argument will
     * happily override those defined in the current bundle. Use with care!</p>
     *
     * @param other the other bundle
     */
    public void mergeWith(final FormatBundle other)
    {
        attributes.putAll(other.attributes);
    }

    /**
     * Get an immutable map of this bundle's attributes
     *
     * @return a map of this bundle instance's attributes
     */
    public Map<String, FormatAttribute> getAttributes()
    {
        return ImmutableMap.copyOf(attributes);
    }

    @Override
    public String toString()
    {
        return "attributes: " + attributes.keySet();
    }
}
