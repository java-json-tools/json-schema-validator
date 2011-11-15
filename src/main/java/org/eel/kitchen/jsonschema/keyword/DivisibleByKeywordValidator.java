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

import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.math.BigDecimal;

/**
 * Keyword validator for the {@code divisibleBy} keyword (draft section 5.24)
 */
public final class DivisibleByKeywordValidator
    extends NumericInstanceKeywordValidator
{
    public DivisibleByKeywordValidator()
    {
        super("divisibleBy");
    }

    @Override
    protected ValidationReport validateLong(final ValidationContext context,
        final long value, final long against)
    {
        final ValidationReport report = context.createReport();

        if (against % value != 0)
            report.addMessage("number is not a multiple of divisibleBy");

        return report;
    }

    @Override
    protected ValidationReport validateDecimal(final ValidationContext context,
        final BigDecimal value, final BigDecimal against)
    {
        final ValidationReport report = context.createReport();

        final BigDecimal remainder = against.remainder(value);

        if (remainder.compareTo(BigDecimal.ZERO) != 0)
            report.addMessage("number is not a multiple of divisibleBy");

        return report;
    }
}
