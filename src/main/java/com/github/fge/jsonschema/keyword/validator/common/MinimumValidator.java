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
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.helpers.NumericValidator;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;

import java.math.BigDecimal;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class MinimumValidator
    extends NumericValidator
{
    private final boolean exclusive;

    public MinimumValidator(final JsonNode digest)
    {
        super("minimum", digest);
        exclusive = digest.path("exclusive").asBoolean(false);
    }

    @Override
    protected void validateLong(final ProcessingReport report,
        final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final long instanceValue = instance.longValue();
        final long longValue = number.longValue();

        if (instanceValue > longValue)
            return;

        if (instanceValue < longValue) {
            report.error(newMsg(data).message(NUMBER_TOO_SMALL).put(keyword, number)
                .put("found", instance));
            return;
        }

        if (!exclusive)
            return;

        report.error(newMsg(data).message(NUMBER_EX_SMALL).put(keyword, number)
            .put("exclusiveMinimum", BooleanNode.TRUE));
    }

    @Override
    protected void validateDecimal(final ProcessingReport report,
        final ValidationData data)
        throws ProcessingException
    {
        final JsonNode instance = data.getInstance().getNode();
        final BigDecimal instanceValue = instance.decimalValue();
        final BigDecimal decimalValue = number.decimalValue();

        final int cmp = instanceValue.compareTo(decimalValue);

        if (cmp > 0)
            return;

        if (cmp < 0) {
            report.error(newMsg(data).message(NUMBER_TOO_SMALL).put(keyword, number)
                .put("found", instance));
            return;
        }

        if (!exclusive)
            return;

        report.error(newMsg(data).message(NUMBER_EX_SMALL).put(keyword, number)
            .put("exclusiveMinimum", BooleanNode.TRUE));
    }
}
