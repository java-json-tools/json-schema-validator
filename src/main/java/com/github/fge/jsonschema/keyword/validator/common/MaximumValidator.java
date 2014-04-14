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

package com.github.fge.jsonschema.keyword.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.helpers.NumericValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.math.BigDecimal;

/**
 * Keyword validator for {@code maximum}
 */
public final class MaximumValidator
    extends NumericValidator
{
    private final boolean exclusive;

    public MaximumValidator(final JsonNode digest)
    {
        super("maximum", digest);
        exclusive = digest.path("exclusive").booleanValue();
    }

    @Override
    protected void validateLong(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final long instanceValue = instance.longValue();
        final long longValue = number.longValue();

        if (instanceValue < longValue)
            return;

        if (instanceValue > longValue) {
            report.error(newMsg(data, bundle, "err.common.maximum.tooLarge")
                .putArgument(keyword, number).putArgument("found", instance));
            return;
        }

        if (!exclusive)
            return;

        report.error(newMsg(data, bundle, "err.common.maximum.notExclusive")
            .putArgument(keyword, number)
            .put("exclusiveMaximum", BooleanNode.TRUE));
    }

    @Override
    protected void validateDecimal(final ProcessingReport report,
        final MessageBundle bundle, final FullData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final BigDecimal instanceValue = instance.decimalValue();
        final BigDecimal decimalValue = number.decimalValue();

        final int cmp = instanceValue.compareTo(decimalValue);

        if (cmp < 0)
            return;

        if (cmp > 0) {
            report.error(newMsg(data, bundle, "err.common.maximum.tooLarge")
                .putArgument(keyword, number).putArgument("found", instance));
            return;
        }

        if (!exclusive)
            return;

        report.error(newMsg(data, bundle, "err.common.maximum.notExclusive")
            .putArgument(keyword, number)
            .put("exclusiveMaximum", BooleanNode.TRUE));
    }
}
