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

package eel.kitchen.jsonschema.validators.format;

import org.codehaus.jackson.JsonNode;

import java.math.BigInteger;

/**
 * <p>Validate a Unix epoch. It can either be an integer or a decimal,
 * and represents, as always, the number of seconds since Jan 1st 1970 00:00:00
 * GMT.</p>
 *
 * <p>This validator checks for overflows: even if Java's
 * <code>System.getCurrentTimeMillis()</code> returns a long,
 * internally the counter is a signed 32 bit integer. It will therefore
 * overflow if the provided number is strictly greater than 2^31 - 1.</p>
 */
public final class UnixEpochFormatValidator
    extends AbstractFormatValidator
{
    /**
     * The right shift to use. If the grabbed BigInteger shifted right by
     * EPOCH_SHIFT is not zero, then we have an overflow.
     */
    private static final int EPOCH_SHIFT = 31;

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final BigInteger epoch = node.getDecimalValue().toBigInteger();

        if (BigInteger.ZERO.equals(epoch.shiftRight(EPOCH_SHIFT)))
            return true;

        messages.add("epoch time would overflow");
        return false;
    }
}
