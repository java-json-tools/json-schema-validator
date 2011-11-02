/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.keyword.format;

import eel.kitchen.jsonschema.ValidationReport;
import org.codehaus.jackson.JsonNode;

import java.math.BigInteger;

public final class UnixEpochValidator
    extends AbstractFormatValidator
{
    private static final int EPOCH_SHIFT = 31;
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    public UnixEpochValidator(final ValidationReport report,
        final JsonNode node)
    {
        super(report, node);
    }

    @Override
    public ValidationReport validate()
    {
        final BigInteger epoch = node.getDecimalValue().toBigInteger()
            .divide(ONE_THOUSAND);

        if (BigInteger.ZERO.compareTo(epoch) > 0) {
            report.addMessage("epoch cannot be negative");
            return report;
        }

        if (!BigInteger.ZERO.equals(epoch.shiftRight(EPOCH_SHIFT)))
            report.addMessage("epoch time would overflow");

        return report;
    }
}
