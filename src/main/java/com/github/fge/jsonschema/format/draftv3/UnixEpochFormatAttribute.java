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

package com.github.fge.jsonschema.format.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;

import java.math.BigInteger;

/**
 * Validator for the {@code utc-millisec} format attribute.
 *
 * <p>As an extra step, this validator also ensures that the number in the
 * instance is not negative, and does not overflow: Java's {@link
 * System#currentTimeMillis()} may return a long, but internally the return code
 * is a signed 32-bit integer, therefore must not be greater than 2^31 - 1.
 * </p>
 */
public final class UnixEpochFormatAttribute
    extends FormatAttribute
{
    private static final FormatAttribute INSTANCE
        = new UnixEpochFormatAttribute();

    /**
     * The maximum bit length of a Unix timestamp value
     */
    private static final int EPOCH_BITLENGTH = 31;

    /**
     * 1000 as a {@link BigInteger}
     */
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    private UnixEpochFormatAttribute()
    {
        super(NodeType.INTEGER, NodeType.NUMBER);
    }

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void checkValue(final String fmt, final ValidationReport report,
        final JsonNode value)
    {
        BigInteger epoch = value.bigIntegerValue();

        final Message.Builder msg;

        if (epoch.signum() == -1) {
            msg = newMsg(fmt).setMessage("epoch cannot be negative")
                .addInfo("value", value);
            report.addMessage(msg.build());
            return;
        }

        epoch = epoch.divide(ONE_THOUSAND);

        if (epoch.bitLength() > EPOCH_BITLENGTH) {
            msg = newMsg(fmt).setMessage("epoch time would overflow")
                .addInfo("value", value);
            report.addMessage(msg.build());
        }
    }
}
