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
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * Keyword validator for the {@code format} keyword (draft section 5.23)
 *
 * <p>This is the only validator which uses a specialized factory (a {@link
 * FormatFactory}) for its own purposes, as it needs to pick a validator
 * matching the format specification.</p>
 */
public final class FormatKeywordValidator
    extends KeywordValidator
{
    private static final FormatKeywordValidator instance
        = new FormatKeywordValidator();

    private static final FormatFactory factory = FormatFactory.getInstance();

    private FormatKeywordValidator()
    {
        super("format");
    }

    public static FormatKeywordValidator getInstance()
    {
        return instance;
    }

    /**
     * Validate against a format specification
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode schema = context.getSchema();

        final String fmt = schema.get(keyword).textValue();

        final Validator validator
            = factory.getFormatValidator(fmt, instance);

        return validator.validate(context, instance);
    }

}
