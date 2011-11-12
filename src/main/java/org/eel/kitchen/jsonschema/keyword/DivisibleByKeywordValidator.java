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

/**
 * Keyword validator for the {@code divisibleBy} keyword (draft section 5.24)
 */
// TODO: specialize validation for "smaller" types (long, double)
public final class DivisibleByKeywordValidator
    extends NumericInstanceKeywordValidator
{
    public DivisibleByKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance, "divisibleBy");
    }

    @Override
    protected void validateLong(final long value, final long against)
    {
        if (against % value != 0)
            report.addMessage("number is not a multiple of divisibleBy");

    }

    @Override
    protected void validateDecimal(final BigDecimal value,
        final BigDecimal against)
    {
        final BigDecimal remainder = against.remainder(value);

        if (remainder.compareTo(BigDecimal.ZERO) != 0)
            report.addMessage("number is not a multiple of divisibleBy");
    }
}
