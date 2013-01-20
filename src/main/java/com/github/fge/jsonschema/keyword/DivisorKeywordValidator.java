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

package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;

import java.math.BigDecimal;

/**
 * Abstract validator for the {@code divisibleBy} (draft v3) and {@code
 * multipleOf} (draft v4) keywords
 */
public abstract class DivisorKeywordValidator
    extends NumericKeywordValidator
{
    protected DivisorKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(keyword, schema);
    }

    @Override
    protected final void validateLong(final ValidationReport report,
        final JsonNode instance)
    {
        final long instanceValue = instance.longValue();
        final long longValue = number.longValue();

        final long remainder = instanceValue % longValue;

        if (remainder == 0L)
            return;

        final Message.Builder msg = newMsg()
            .setMessage("number is not a multiple of " + keyword)
            .addInfo("value", instance).addInfo("divisor", number);
        report.addMessage(msg.build());
    }

    @Override
    protected final void validateDecimal(final ValidationReport report,
        final JsonNode instance)
    {
        final BigDecimal instanceValue = instance.decimalValue();
        final BigDecimal decimalValue = number.decimalValue();

        final BigDecimal remainder = instanceValue.remainder(decimalValue);

        /*
         * We cannot use equality! As far as BigDecimal goes,
         * "0" and "0.0" are NOT equal. But .compareTo() returns the correct
         * result.
         */
        if (remainder.compareTo(BigDecimal.ZERO) == 0)
            return;

        final Message.Builder msg = newMsg()
            .setMessage("number is not a multiple of " + keyword)
            .addInfo("value", instance).addInfo("divisor", number);
        report.addMessage(msg.build());
    }
}
