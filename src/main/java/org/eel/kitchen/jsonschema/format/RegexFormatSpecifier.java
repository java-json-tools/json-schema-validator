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
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.util.RhinoHelper;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code regex} format specification
 *
 * <p>Again, here, we do <b>not</b> use {@link java.util.regex} because it
 * does not fit the bill.</p>
 *
 * @see RhinoHelper
 */
public final class RegexFormatSpecifier
    extends FormatSpecifier
{
    private static final FormatSpecifier instance
        = new RegexFormatSpecifier();

    private RegexFormatSpecifier()
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
        if (RhinoHelper.regexIsValid(value.textValue()))
            return;

        final ValidationMessage.Builder msg = newMsg(fmt)
            .setMessage("string is not a valid ECMA 262 regular expression")
            .addInfo("value", value);
        report.addMessage(msg.build());
    }
}
