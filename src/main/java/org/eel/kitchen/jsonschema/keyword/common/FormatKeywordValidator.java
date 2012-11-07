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

package org.eel.kitchen.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code format} keyword
 *
 * <p>The {@code format} keyword is a particular beast, since in draft v3, it is
 * the only validation keyword able to do <i>semantic</i> analysis of an
 * instance.</p>
 *
 * <p>This library only supports a subset of all format attributes defined by
 * draft v3. Other format attributes, apart from {@code color} and {@code style}
 * (for which support is deliberately ommitted), are in another library
 * dependent on this one (and also reachable by Maven users): <a
 * href="https://github.com/fge/json-schema-formats">json-schema-formats</a>.
 * </p>
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
        final FormatAttribute attribute = context.getFormat(fmt);

        if (attribute == null)
            return;

        attribute.validate(fmt, context, report, instance);
    }

    @Override
    public String toString()
    {
        return keyword + ": " + fmt;
    }
}
