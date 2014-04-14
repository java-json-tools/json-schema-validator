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

package com.github.fge.jsonschema.keyword.digest.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;
import com.github.fge.jsonschema.keyword.validator.helpers.NumericValidator;

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
