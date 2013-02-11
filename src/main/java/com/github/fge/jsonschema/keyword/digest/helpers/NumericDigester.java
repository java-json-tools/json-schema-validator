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

package com.github.fge.jsonschema.keyword.digest.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.validator.helpers.NumericValidator;
import com.github.fge.jsonschema.util.NodeType;

import java.math.BigDecimal;

/**
 * A specialized digester for numeric keywords
 *
 * <p>This digester ensures that, for instance, values {@code 1}, {@code 1.0}
 * and {@code 1.00} produce the same digest. It also stores another important
 * information: whether that number can be reliably represented as a {@code
 * long}. If this is not the case, for accuracy reasons, {@link BigDecimal} is
 * used.</p>
 *
 * @see NumericValidator
 */
public abstract class NumericDigester
    extends AbstractDigester
{
    protected NumericDigester(final String keyword)
    {
        super(keyword, NodeType.INTEGER, NodeType.NUMBER);
    }

    private static boolean valueIsLong(final JsonNode node)
    {
        if (!node.canConvertToLong())
            return false;

        if (NodeType.getNodeType(node) == NodeType.INTEGER)
            return true;

        return node.decimalValue().remainder(BigDecimal.ONE)
            .compareTo(BigDecimal.ZERO) == 0;
    }

    protected final ObjectNode digestedNumberNode(final JsonNode schema)
    {
        final ObjectNode ret = FACTORY.objectNode();

        final JsonNode node = schema.get(keyword);
        final boolean isLong = valueIsLong(node);
        ret.put("valueIsLong", isLong);

        if (isLong) {
            ret.put(keyword, node.canConvertToInt()
                ? FACTORY.numberNode(node.intValue())
                : FACTORY.numberNode(node.longValue()));
            return ret;
        }

        final BigDecimal decimal = node.decimalValue();
        ret.put(keyword, decimal.scale() == 0
            ? FACTORY.numberNode(decimal.toBigIntegerExact())
            : node);

        return ret;
    }
}
