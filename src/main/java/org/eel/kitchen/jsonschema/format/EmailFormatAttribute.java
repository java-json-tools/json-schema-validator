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
import org.eel.kitchen.jsonschema.main.ValidationFeature;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validator for the {@code email} format specification.
 *
 * <p>Note: even though the RFC covering email addresses does not require that
 * emails have a domain part, this implementation requires that they have one
 * by default (this is more in line with user expectations). You can enforce
 * strict RFC compliance by setting the {@link
 * ValidationFeature#STRICT_RFC_CONFORMANCE} validation feature before building
 * your schema factory.</p>
 *
 * @see ValidationFeature
 */
public final class EmailFormatAttribute
    extends FormatAttribute
{
    private static final FormatAttribute instance = new EmailFormatAttribute();

    private EmailFormatAttribute()
    {
        super(NodeType.STRING);
    }

    public static FormatAttribute getInstance()
    {
        return instance;
    }

    @Override
    public void checkValue(final String fmt, final ValidationContext ctx,
        final ValidationReport report, final JsonNode value)
    {
        // Yup, that is kind of misnamed. But the problem is with the
        // InternetAddress constructor in the first place which "enforces" a
        // syntax which IS NOT strictly RFC compliant.
        final boolean strictRFC
            = ctx.hasFeature(ValidationFeature.STRICT_RFC_CONFORMANCE);

        try {
            // Which means we actually invert it.
            new InternetAddress(value.textValue(), !strictRFC);
        } catch (AddressException ignored) {
            final Message.Builder msg = newMsg(fmt)
                .setMessage("string is not a valid email address")
                .addInfo("value", value);
            report.addMessage(msg.build());
        }
    }
}
