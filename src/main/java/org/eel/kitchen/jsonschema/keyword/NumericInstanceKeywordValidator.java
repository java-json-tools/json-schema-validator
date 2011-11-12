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
import org.eel.kitchen.jsonschema.context.ValidationContext;

import java.math.BigDecimal;

public abstract class NumericInstanceKeywordValidator
    extends SimpleKeywordValidator
{
    protected final JsonNode schemaValue;

    /**
     * Constructor
     *
     * @param context  the context to use
     * @param instance the instance to validate
     */
    protected NumericInstanceKeywordValidator(final ValidationContext context,
        final JsonNode instance, final String keyword)
    {
        super(context, instance);
        schemaValue = context.getSchemaNode().get(keyword);
    }

    @Override
    protected final void validateInstance()
    {
        final BigDecimal value = schemaValue.getDecimalValue();
        final BigDecimal against = instance.getDecimalValue();

        try {
            validateLong(value.longValueExact(), against.longValueExact());
        } catch (ArithmeticException ignored) {
            validateDecimal(value, against);
        }
    }

    protected abstract void validateLong(final long value, final long against);

    protected abstract void validateDecimal(final BigDecimal value,
        final BigDecimal against);
}
