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
import org.eel.kitchen.jsonschema.main.ValidationFeature;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code host-name} format specification
 *
 * <p>Note: even though the RFCs covering hostnames do not require that
 * hostnames have a domain part, this implementation requires that they have
 * one by default (this is more in line with user expectations). You can enforce
 * strict RFC compliance by setting the {@link
 * ValidationFeature#STRICT_RFC_CONFORMANCE} validation feature before building
 * your schema factory.</p>
 *
 * <p>Guava's {@link InternetDomainName} is used for validation.</p>
 *
 * @see ValidationFeature
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
    public void checkValue(final String fmt, final ValidationContext ctx,
        final ValidationReport report, final JsonNode value)
    {
        final ValidationMessage.Builder msg = newMsg(fmt)
            .setMessage("string is not a valid hostname")
            .addInfo("value", value);

        final InternetDomainName hostname;
        try {
            hostname = InternetDomainName.from(value.textValue());
        } catch (IllegalArgumentException ignored) {
            report.addMessage(msg.build());
            return;
        }

        if (ctx.hasFeature(ValidationFeature.STRICT_RFC_CONFORMANCE))
            return;

        if (!hostname.hasParent())
            report.addMessage(msg.build());
    }
}
