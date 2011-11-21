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
import org.eel.kitchen.jsonschema.keyword.NumericInstanceKeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.math.BigDecimal;

/**
 * Keyword validator for the {@code minimum} and {@code exclusiveMinimum}
 * keywords (draft sections 5.9 and 5.11)
 */
public final class MinimumKeywordValidator
    extends NumericInstanceKeywordValidator
{
    public MinimumKeywordValidator()
    {
        super("minimum");
    }

    @Override
    protected ValidationReport validateLong(final ValidationContext context,
        final long value, final long against)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchema();
        final boolean exclusiveMinimum = schema.path("exclusiveMinimum")
            .asBoolean(false);

        final long cmp = value - against;

        if (cmp > 0)
            report.fail("number is lower than the required minimum");
        else  if (cmp == 0 && exclusiveMinimum)
            report.fail("number is not strictly greater than the required "
                + "minimum");

        return report;
    }

    @Override
    protected ValidationReport validateDecimal(final ValidationContext context,
        final BigDecimal value, final BigDecimal against)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchema();
        final boolean exclusiveMinimum = schema.path("exclusiveMinimum")
            .asBoolean(false);

        final int cmp = value.compareTo(against);

        if (cmp > 0)
            report.fail("number is lower than the required minimum");
        else if (cmp == 0 && exclusiveMinimum)
            report.fail("number is not strictly greater than the required "
                + "minimum");

        return report;
    }
}
