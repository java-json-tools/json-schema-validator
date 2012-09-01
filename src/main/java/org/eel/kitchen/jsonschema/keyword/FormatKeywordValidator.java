/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
 * Copyright (c) 2012, Corey Sciuto <corey.sciuto@gmail.com>
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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.format.FormatSpecifier;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code format} keyword
 *
 * <p>This keyword is scheduled for disappearance in draft v4. However,
 * some people have raised concerns about this.</p>
 *
 * <p>All format specifiers from draft v3 are supported except {@code style}
 * and {@code color} (which validate an entire CSS 2.1 style and color
 * respectively!).</p>
 *
 * <p>There is support here for one custom specifier: {@code date-time-ms}. The
 * v3 draft specifies that {@code date-time} should match the pattern
 * {@code YYYY-MM-DDThh:mm:ssZ}.  {@code date-time-ms} extends that to a format
 * supporting milliseconds: {@code YYYY-MM-DDThh:mm:ss.SSSZ}.</p>
 *
 * @see org.eel.kitchen.jsonschema.format
 */
public final class FormatKeywordValidator
    extends KeywordValidator
{
    // The format attribute
    private final String fmt;

    public FormatKeywordValidator(final JsonNode schema)
    {
        super("format", NodeType.values());
        fmt = schema.get(keyword).textValue();
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final FormatSpecifier specifier = context.getFormat(fmt);

        if (specifier == null)
            return;

        specifier.validate(fmt, context, report, instance);
    }

    @Override
    public String toString()
    {
        return keyword + ": " + fmt;
    }
}
