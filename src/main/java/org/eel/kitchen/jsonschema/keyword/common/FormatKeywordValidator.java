/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.factories.FormatFactory;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
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
     *
     * <p>If the format specification is unknown, the validation is a failure
     * (FIXME: not what the draft says). If the type of the instance cannot
     * be validated by the matching validator, the validation is a success.
     *
     * @see FormatFactory#getFormatValidator(ValidationContext, String,
     * JsonNode)
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonNode schema = context.getSchema();

        final String fmt = schema.get(keyword).getTextValue();

        final Validator validator
            = context.getFormatValidator(fmt, instance);

        return validator.validate(context, instance);
    }

}
