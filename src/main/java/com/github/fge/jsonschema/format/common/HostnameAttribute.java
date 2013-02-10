/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.format.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.net.InternetDomainName;

import static com.github.fge.jsonschema.messages.FormatMessages.*;

/**
 * Validator for the {@code host-name} format attribute.
 *
 * <p><b>Important note</b>: the basis for host name format validation is <a
 * href="http://tools.ietf.org/html/rfc1034">RFC 1034</a>. The RFC does <b>not
 * </b> require that a host name have more than one domain name component. As
 * such, {@code foo} <b>is</b> a valid hostname.</p>
 *
 * <p>Guava's {@link InternetDomainName} is used for validation.</p>
 */
public final class HostnameAttribute
    extends AbstractFormatAttribute
{
    private static final FormatAttribute INSTANCE = new HostnameAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private HostnameAttribute()
    {
        super("host-name", NodeType.STRING);
    }

    @Override
    public void validate(final ProcessingReport report,
        final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();

        try {
            InternetDomainName.from(instance.textValue());
        } catch (IllegalArgumentException ignored) {
            report.error(newMsg(data, INVALID_HOSTNAME));
        }
    }
}
