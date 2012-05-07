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

package org.eel.kitchen.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.NumericInstanceKeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.math.BigDecimal;

/**
 * Keyword validator for both the {@code maximum} and {@code
 * exclusiveMaximum} keywords (draft sections 5.10 and 5.12)
 */
public final class MaximumKeywordValidator
    extends NumericInstanceKeywordValidator
{
    private static final MaximumKeywordValidator instance
        = new MaximumKeywordValidator();

    private MaximumKeywordValidator()
    {
        super("maximum");
    }

    public static MaximumKeywordValidator getInstance()
    {
        return instance;
    }

    @Override
    protected ValidationReport validateLong(final ValidationContext context,
        final long value, final long against)
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchema();
        final boolean exclusive = schema.path("exclusiveMaximum")
            .asBoolean(false);

        final long cmp = value - against;

        if (cmp < 0L)
            report.message("number is greater than the required maximum");
        else if (cmp == 0L && exclusive)
            report.message(
                "number is not strictly lower than the required " + "maximum");

        return report;
    }

    @Override
    protected ValidationReport validateDecimal(final ValidationContext context,
        final BigDecimal value, final BigDecimal against)
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchema();
        final boolean exclusive = schema.path("exclusiveMaximum")
            .asBoolean(false);

        final int cmp = value.compareTo(against);

        if (cmp < 0)
            report.message("number is greater than the required maximum");
        else if (cmp == 0 && exclusive)
            report.message(
                "number is not strictly lower than the required " + "maximum");

        return report;
    }
}
