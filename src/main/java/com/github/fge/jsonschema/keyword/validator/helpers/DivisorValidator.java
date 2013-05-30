/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.keyword.validator.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.math.BigDecimal;

/**
 * Helper keyword validator for draft v4's {@code multipleOf} and draft v3's
 * {@code divisibleBy}
 */
public abstract class DivisorValidator
    extends NumericValidator
{
    protected DivisorValidator(final String keyword, final JsonNode digest)
    {
        super(keyword, digest);
    }

    @Override
    protected final void validateLong(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final JsonNode node = data.getInstance().getNode();
        final long instanceValue = node.longValue();
        final long longValue = number.longValue();

        final long remainder = instanceValue % longValue;

        if (remainder == 0L)
            return;

        report.error(newMsg(data, bundle, "NON_ZERO_DIVISION_REMAINDER")
            .put("value", node).put("divisor", number));
    }

    @Override
    protected final void validateDecimal(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final JsonNode node = data.getInstance().getNode();
        final BigDecimal instanceValue = node.decimalValue();
        final BigDecimal decimalValue = number.decimalValue();

        final BigDecimal remainder = instanceValue.remainder(decimalValue);

        /*
         * We cannot use equality! As far as BigDecimal goes,
         * "0" and "0.0" are NOT equal. But .compareTo() returns the correct
         * result.
         */
        if (remainder.compareTo(BigDecimal.ZERO) == 0)
            return;

        report.error(newMsg(data, bundle, "NON_ZERO_DIVISION_REMAINDER")
            .put("value", node).put("divisor", number));
    }
}
