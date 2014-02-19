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
