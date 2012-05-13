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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.math.BigDecimal;

public abstract class NumericKeywordValidator
    extends KeywordValidator
{
    protected final BigDecimal decimalValue;

    protected final long longValue;

    protected final boolean isLong;

    protected NumericKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(NodeType.INTEGER, NodeType.NUMBER);
        final JsonNode node = schema.get(keyword);

        isLong = valueIsLong(node);
        decimalValue = node.decimalValue();
        longValue = node.longValue();
    }

    protected abstract void validateLong(final ValidationReport report,
        final long instanceValue);

    protected abstract void validateDecimal(final ValidationReport report,
        final BigDecimal instanceValue);

    @Override
    public final void validate(final ValidationReport report,
        final JsonNode instance)
    {
        if (valueIsLong(instance) && isLong)
            validateLong(report, instance.longValue());
        else
            validateDecimal(report, instance.decimalValue());
    }

    private static boolean valueIsLong(final JsonNode node)
    {
        return NodeType.getNodeType(node) == NodeType.INTEGER
            && node.canConvertToLong();
    }
}
