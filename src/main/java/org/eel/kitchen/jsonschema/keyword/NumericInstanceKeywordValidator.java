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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;

import java.math.BigDecimal;

/**
 * Keyword validator specialized in numeric instances keyword validation
 *
 * <p>This class uses Java's {@link long} primitive type if both the value
 * of the keyword and the instance fit within this type,
 * otherwise it uses {@link BigDecimal}, even for numbers which would fit
 * within a float or a double. The reason is, for example,
 * for a keyword like {@code divisibleBy}: a rounding error there can lead
 * to wrong results.</p>
 */
public abstract class NumericInstanceKeywordValidator
    extends KeywordValidator
{
    /**
     * Constructor
     *
     * @param context  the context to use
     * @param instance the instance to validate
     * @param keyword the matching JSON schema keyword
     */
    protected NumericInstanceKeywordValidator(final String keyword)
    {
        super(keyword);
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode schema = context.getSchemaNode();

        final BigDecimal value = schema.get(keyword).getDecimalValue();
        final BigDecimal against = instance.getDecimalValue();

        try {
            return validateLong(context, value.longValueExact(),
                against.longValueExact());
        } catch (ArithmeticException ignored) {
            return validateDecimal(context, value, against);
        }
    }

    protected abstract ValidationReport validateLong(
        final ValidationContext context, final long value, final long against);

    protected abstract ValidationReport validateDecimal(
        final ValidationContext context, final BigDecimal value,
        final BigDecimal against);
}
