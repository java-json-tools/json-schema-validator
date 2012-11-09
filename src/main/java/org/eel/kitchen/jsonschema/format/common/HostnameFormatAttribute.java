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

package org.eel.kitchen.jsonschema.format.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.InternetDomainName;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;

/**
 * Validator for the {@code host-name} format specification
 *
 * <p>Note: a hostname is not required to have two or more components. As such,
 * {@code foo} <b>is</b> a valid hostname.</p>
 *
 * <p>Guava's {@link InternetDomainName} is used for validation.</p>
 */
public final class HostnameFormatAttribute
    extends FormatAttribute
{
    private static final FormatAttribute instance
        = new HostnameFormatAttribute();

    private HostnameFormatAttribute()
    {
        super(NodeType.STRING);
    }

    public static FormatAttribute getInstance()
    {
        return instance;
    }

    @Override
    public void checkValue(final String fmt, final ValidationReport report,
        final JsonNode value)
    {
        try {
            InternetDomainName.from(value.textValue());
        } catch (IllegalArgumentException ignored) {
            final Message.Builder msg = newMsg(fmt)
                .setMessage("string is not a valid hostname")
                .addInfo("value", value);
            report.addMessage(msg.build());
        }
    }
}
