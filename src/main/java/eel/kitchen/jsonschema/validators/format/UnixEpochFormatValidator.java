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

import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.math.BigInteger;

public final class UnixEpochFormatValidator
    extends AbstractValidator
{
    private static final BigInteger ZERO = new BigInteger("0");
    private static final int EPOCH_SHIFT = 31;

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        final BigInteger epoch = node.getDecimalValue().toBigInteger();

        if (ZERO.equals(epoch.shiftRight(EPOCH_SHIFT)))
            return true;

        messages.add("epoch time would overflow");
        return false;
    }
}
