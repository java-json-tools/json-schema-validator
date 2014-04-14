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

package com.github.fge.jsonschema.keyword.validator.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.processors.data.FullData;
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

        report.error(newMsg(data, bundle, "err.common.divisor.nonZeroRemainder")
            .putArgument("value", node).putArgument("divisor", number));
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

        report.error(newMsg(data, bundle, "err.common.divisor.nonZeroRemainder")
            .putArgument("value", node).putArgument("divisor", number));
    }
}
