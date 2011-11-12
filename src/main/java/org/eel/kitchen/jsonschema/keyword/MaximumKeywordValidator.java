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
 * Keyword validator for both the {@code maximum} and {@code
 * exclusiveMaximum} keywords (draft sections 5.10 and 5.12)
 */
public final class MaximumKeywordValidator
    extends NumericInstanceKeywordValidator
{

    /**
     * Is the maximum exclusive?
     */
    private final boolean exclusiveMaximum;

    public MaximumKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance, "maximum");
        exclusiveMaximum = schema.path("exclusiveMaximum").asBoolean(false);
    }

    @Override
    protected void validateLong(final long value, final long against)
    {
        final long cmp = value - against;

        if (cmp < 0) {
            report.addMessage("number is greater than the required maximum");
            return;
        }

        if (cmp == 0 && exclusiveMaximum)
            report.addMessage("number is not strictly lower than "
                + "the required maximum");
    }

    @Override
    protected void validateDecimal(final BigDecimal value,
        final BigDecimal against)
    {
        final int cmp = value.compareTo(against);

        if (cmp < 0) {
            report.addMessage("number is greater than the required maximum");
            return;
        }

        if (cmp == 0 && exclusiveMaximum)
            report.addMessage("number is not strictly lower than "
                + "the required maximum");
    }
}
