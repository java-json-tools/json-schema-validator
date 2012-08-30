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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;

import java.math.BigDecimal;

/**
 * Validator for the {@code maximum} keyword
 *
 * <p>It also pairs with {@code exclusiveMaximum}, if found. The latter has
 * no signification in and of itself without {@code maximum}.
 * </p>
 */
public final class MaximumKeywordValidator
    extends NumericKeywordValidator
{
    private final boolean exclusive;

    public MaximumKeywordValidator(final JsonNode schema)
    {
        super("maximum", schema);
        /* You have to love Jackson! */
        exclusive = schema.path("exclusiveMaximum").asBoolean(false);
    }

    @Override
    protected void validateLong(final ValidationReport report,
        final JsonNode instance)
    {
        final long instanceValue = instance.longValue();
        final long longValue = number.longValue();

        if (instanceValue < longValue)
            return;

        final ValidationMessage.Builder msg = newMsg()
            .addInfo(keyword, number).addInfo("found", instance);

        if (instanceValue > longValue) {
            msg.setMessage("number is greater than the required maximum");
            report.addMessage(msg.build());
            return;
        }

        if (!exclusive)
            return;

        msg.setMessage("number is not strictly lower than the required maximum")
            .addInfo("exclusiveMaximum", nodeFactory.booleanNode(true));
        report.addMessage(msg.build());
    }

    @Override
    protected void validateDecimal(final ValidationReport report,
        final JsonNode instance)
    {
        final BigDecimal instanceValue = instance.decimalValue();
        final BigDecimal decimalValue = number.decimalValue();

        final int cmp = instanceValue.compareTo(decimalValue);

        if (cmp < 0)
            return;

        final ValidationMessage.Builder msg = newMsg().addInfo(keyword, number)
            .addInfo("found", instance);

        if (cmp > 0) {
            msg.setMessage("number is greater than the required maximum");
            report.addMessage(msg.build());
            return;
        }

        if (!exclusive)
            return;

        msg.setMessage("number is not strictly lower than the required maximum")
            .addInfo("exclusiveMaximum", nodeFactory.booleanNode(true));
        report.addMessage(msg.build());
    }
}
