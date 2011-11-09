/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.factories.FormatFactory;

/**
 * <p>Keyword validator for the {@code format} keyword (draft section
 * 5.23).</p>
 *
 * <p>This is the only validator which uses a builtin factory (a {@link
 * FormatFactory}) for its own purposes, as it needs to pick a validator
 * matching the format specification.</p>
 */
//TODO: inline validators?
public final class FormatKeywordValidator
    extends SimpleKeywordValidator
{
    /**
     * The format factory
     */
    private final FormatFactory formatFactory;

    public FormatKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
        formatFactory = new FormatFactory(context);
    }

    /**
     * Validate against a format specification. If the specification is
     * unknown, the validation is a failure (FIXME: not what the draft says).
     * If the type of the instance cannot be validated by the matching
     * validator, the validation is a success.
     *
     * @see FormatFactory#getFormatValidator(String, JsonNode)
     */
    @Override
    protected void validateInstance()
    {
        final String fmt = schema.get("format").getTextValue();

        final Validator validator
            = formatFactory.getFormatValidator(fmt, instance);

        report.mergeWith(validator.validate());
    }

}
