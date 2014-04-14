/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.format.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.format.AbstractFormatAttribute;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.math.BigInteger;

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
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();

        BigInteger epoch = instance.bigIntegerValue();

        if (epoch.signum() == -1) {
            report.warn(newMsg(data, bundle, "warn.format.epoch.negative")
                .putArgument("value", instance));
            return;
        }

        epoch = epoch.divide(ONE_THOUSAND);

        if (epoch.bitLength() > EPOCH_BITLENGTH)
            report.warn(newMsg(data, bundle, "warn.format.epoch.overflow")
                .putArgument("value", instance));
    }
}
