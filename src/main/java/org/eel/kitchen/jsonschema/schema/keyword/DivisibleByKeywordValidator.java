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

package org.eel.kitchen.jsonschema.schema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.schema.ValidationReport;

import java.math.BigDecimal;

public final class DivisibleByKeywordValidator
    extends NumericKeywordValidator
{
    public DivisibleByKeywordValidator(final JsonNode schema)
    {
        super("divisibleBy", schema);
    }

    @Override
    protected void validateLong(final ValidationReport report,
        final long instanceValue)
    {
        if (instanceValue % longValue != 0)
            report.addMessage("instance is not a multiple of divisibleBy");
    }

    @Override
    protected void validateDecimal(final ValidationReport report,
        final BigDecimal instanceValue)
    {
        final BigDecimal remainder = instanceValue.remainder(decimalValue);

        if (remainder.compareTo(BigDecimal.ZERO) != 0)
            report.addMessage("instance is not a multiple of divisibleBy");
    }
}
