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
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

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
     * @param keyword the matching JSON schema keyword
     */
    protected NumericInstanceKeywordValidator(final String keyword)
    {
        super(keyword);
    }

    /**
     * Main validation function
     *
     * <p>It is here that the split is done between {@link #validateLong
     * (ValidationContext, long, long)} and {@link #validateDecimal
     * (ValidationContext, BigDecimal, BigDecimal)}.</p>
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @return the validation report
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode schema = context.getSchemaNode();

        final BigDecimal value = schema.get(keyword).getDecimalValue();
        final BigDecimal against = instance.getDecimalValue();

        /*
         * Unfortunately, there is a "bug" in Jackson: if you use
         * USE_BIG_INTEGER_FOR_INT as a deserialization option, .isLong(),
         * or .isInt(), will always return false :/
         *
         * We have to do the following to work around it... In the future,
         * hopefully, we'll have .fitsXXX() for primitive types.
         */
        try {
            return validateLong(context, value.longValueExact(),
                against.longValueExact());
        } catch (ArithmeticException ignored) {
            return validateDecimal(context, value, against);
        }
    }

    /**
     * Validate a numeric instance if both the schema value and this instance
     * fit into the {@code long} primitive type
     *
     * @param context the context
     * @param value the schema value
     * @param against the instance value
     * @return the report
     */
    protected abstract ValidationReport validateLong(
        final ValidationContext context, final long value, final long against);

    /**
     * Validate a numeric instance if itself or the schema value don't fit in
     * a {@code long}
     *
     * @param context the context
     * @param value the schema value
     * @param against the instance value
     * @return the report
     */
    protected abstract ValidationReport validateDecimal(
        final ValidationContext context, final BigDecimal value,
        final BigDecimal against);
}
