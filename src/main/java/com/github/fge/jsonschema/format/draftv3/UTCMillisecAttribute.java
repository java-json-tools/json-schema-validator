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

package com.github.fge.jsonschema.format.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.NodeType;

import java.math.BigInteger;

import static com.github.fge.jsonschema.messages.FormatMessages.*;

/**
 * Validator for the {@code utc-millisec} format attribute.
 *
 * <p>Note that there is no restriction on the number value at all. However,
 * this attributes perform extra checks and <b>warns</b> (ie, does not report
 * an error) in the following situations:</p>
 *
 * <ul>
 *     <li>the number is negative;</li>
 *     <li>the number, divided by 1000, is greater than 2^31 - 1.</li>
 * </ul>
 */
public final class UTCMillisecAttribute
    extends AbstractFormatAttribute
{
    /**
     * The maximum bit length of a Unix timestamp value
     */
    private static final int EPOCH_BITLENGTH = 31;

    /**
     * 1000 as a {@link BigInteger}
     */
    private static final BigInteger ONE_THOUSAND = new BigInteger("1000");

    private static final FormatAttribute INSTANCE = new UTCMillisecAttribute();

    public static FormatAttribute getInstance()
    {
        return INSTANCE;
    }

    private UTCMillisecAttribute()
    {
        super("utc-millisec", NodeType.INTEGER, NodeType.NUMBER);
    }

    @Override
    public void validate(final ProcessingReport report,
        final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();

        BigInteger epoch = instance.bigIntegerValue();

        if (epoch.signum() == -1) {
            report.warn(newMsg(data, EPOCH_NEGATIVE));
            return;
        }

        epoch = epoch.divide(ONE_THOUSAND);

        if (epoch.bitLength() > EPOCH_BITLENGTH)
            report.warn(newMsg(data, EPOCH_OVERFLOW));
    }
}
