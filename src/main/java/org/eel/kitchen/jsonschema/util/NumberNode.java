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

package org.eel.kitchen.jsonschema.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Wrapper clas over Jackson's {@link NumericNode} and derivates
 *
 * <p>This class is a wrapper over all {@link NumericNode} instances and
 * overrides {@link Object#equals(Object)} and {@link Object#hashCode()}, all
 * the while retaining all other characteristics of the underlying node.</p>
 *
 * <p>The reason for this is to allow for mathematical numeric equality: with
 * JSON Schema, {@code 1} and {@code 1.0}, for instance, are equal, but they are
 * not of the same type.</p>
 */
public final class NumberNode
    extends NumericNode
{
    private static final NumericNode ZERO
        = DecimalNode.valueOf(BigDecimal.ZERO);

    private final NumericNode node;
    private final int hashCode;

    /**
     * Sole constructor
     *
     * <p>The argument is a {@link JsonNode} because we need our own {@link
     * JsonNodeFactory} implementation to generate nodes from numeric Java types
     * ({@link Byte}, {@link Integer} and others), and these return instances of
     * {@link ValueNode} instead of {@link NumericNode}.</p>
     *
     * <p>Decimal 0 is special cased: it is unconditionally replaced with {@link
     * BigDecimal#ZERO} for hash code consistency ({@code new BigDecimal("0.0")}
     * has hashcode 1 and we want 0 to be consistent with integer value 0 and,
     * of course, respect the equals/hashCode contract). As a bonus, this allows
     * to use {@link BigDecimal#equals(Object)} instead of {@link
     * BigDecimal#compareTo(BigDecimal)} in {@link #equals(Object)}, since only
     * zero could cause trouble here.</p>
     *
     * <p>The hashcode is precomputed as being equal to the hash code of the
     * node's {@link NumericNode#decimalValue()}, even if this is an integer
     * node.</p>
     *
     * @param node the node
     * @throws IllegalArgumentException the node is not a numeric node ({@link
     * JsonNode#isNumber()} returns {@code false})
     */
    public NumberNode(final JsonNode node)
    {
        Preconditions.checkArgument(node.isNumber(),
            "only numeric nodes are supported");

        final BigDecimal decimal = node.decimalValue();

        if (node.isIntegralNumber()) {
            this.node = (NumericNode) node;
            hashCode = decimal.hashCode();
            return;
        }

        // It is a decimal: check if it is a DecimalNode, and check for zero
        Preconditions.checkArgument(node.getClass() == DecimalNode.class,
            "only DecimalNode instances are supported for floating point "
                + "numbers");

        if (BigDecimal.ZERO.compareTo(decimal) == 0) {
            this.node = ZERO;
            hashCode = 0;
        } else {
            this.node = (NumericNode) node;
            hashCode = decimal.hashCode();
        }
    }

    @Override
    public JsonParser.NumberType numberType()
    {
        return node.numberType();
    }

    @Override
    public void serialize(final JsonGenerator jgen,
        final SerializerProvider provider)
        throws IOException
    {
        node.serialize(jgen, provider);
    }

    @Override
    public Number numberValue()
    {
        return node.numberValue();
    }

    @Override
    public int intValue()
    {
        return node.intValue();
    }

    @Override
    public long longValue()
    {
        return node.longValue();
    }

    @Override
    public double doubleValue()
    {
        return node.doubleValue();
    }

    @Override
    public BigDecimal decimalValue()
    {
        return node.decimalValue();
    }

    @Override
    public BigInteger bigIntegerValue()
    {
        return node.bigIntegerValue();
    }

    @Override
    public boolean canConvertToInt()
    {
        return node.canConvertToInt();
    }

    @Override
    public boolean canConvertToLong()
    {
        return node.canConvertToLong();
    }

    @Override
    public String asText()
    {
        return node.asText();
    }

    @Override
    public JsonToken asToken()
    {
        return node.asToken();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        final NumericNode otherNode = ((NumberNode) o).node;
        if (node.isIntegralNumber() && otherNode.isIntegralNumber())
            return node.equals(otherNode);
        // This works since decimal nodes are normalized
        return node.decimalValue().equals(otherNode.decimalValue());
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    @Override
    public String toString()
    {
        return node.toString();
    }
}
