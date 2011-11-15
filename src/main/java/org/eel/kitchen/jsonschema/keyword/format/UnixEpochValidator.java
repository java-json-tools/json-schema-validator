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

package org.eel.kitchen.jsonschema.keyword.format;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.math.BigInteger;

/**
 * Validator for the "utc-millisec" format specification
 *
 * <p>As an extra step, this validator also ensures that the number in the
 * instance is not negative, and does not overflow: Java's {@link
 * System#currentTimeMillis()} may return a long, but internally the return
 * code is a signed 32-bit integer, therefore must not be greater than 2^31 -
 * 1.
 * </p>
 */
public final class UnixEpochValidator
    extends FormatValidator
{
    /**
     * The maximum bit length of a Unix timestamp value
     */
    private static final int EPOCH_BITLENGTH = 31;

    /**
     * 1000 as a {@link BigInteger}
     */
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();

        BigInteger epoch = instance.getBigIntegerValue();

        if (epoch.signum() == -1) {
            report.addMessage("epoch cannot be negative");
            return report;
        }

        epoch = epoch.divide(ONE_THOUSAND);

        if (epoch.bitLength() > EPOCH_BITLENGTH)
            report.addMessage("epoch time would overflow");

        return report;
    }
}
