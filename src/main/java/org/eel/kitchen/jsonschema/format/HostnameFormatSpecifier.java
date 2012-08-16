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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.InternetDomainName;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;

/**
 * Validator for the {@code host-name} format specification
 *
 * <p>Note: even though non FQDN hostnames are valid stricto sensu,
 * this implementation considers non fully-qualified hostnames as invalid.
 * While this contradicts the RFC, this is more in line with user expectations.
 * </p>
 *
 * <p>Guava's {@link InternetDomainName} is used for validation.</p>
 */
public final class HostnameFormatSpecifier
    extends FormatSpecifier
{
    private static final FormatSpecifier instance
        = new HostnameFormatSpecifier();

    private HostnameFormatSpecifier()
    {
        super(NodeType.STRING);
    }

    public static FormatSpecifier getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final List<String> messages, final JsonNode value)
    {
        try {
            if (!InternetDomainName.from(value.textValue()).hasParent())
                messages.add("string is not a valid hostname");
        } catch (IllegalArgumentException ignored) {
            messages.add("string is not a valid hostname");
        }
    }
}
