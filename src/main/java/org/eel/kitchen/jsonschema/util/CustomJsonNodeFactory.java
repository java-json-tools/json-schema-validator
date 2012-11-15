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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Custom {@link JsonNodeFactory}, for the needs of JSON Schema
 *
 * <p>Jackson's {@link JsonNode} has a sound {@code .equals()} and {@code
 * .hashCode()} implementation all around, for all nodes, for all inputs, which
 * is how it should be. However, there is one point where this is detrimental to
 * JSON Schema: numeric value equality.</p>
 *
 * <p>Specifically, for numeric values, two values should be considered equal if
 * they are <i>mathematically</i> equal. But the base implementation does not
 * consider, for instance, {@code 1.0} and {@code 1} to be the same. According
 * to JSON Schema, they are.</p>
 *
 * <p>As such, we override the default node factory so as to strip all 0  decimal
 * digits off any floating point numeric instance, and if the resulting scale is
 * zero, we make it an appropriate integer type value instead (either of an
 * {@link IntNode}, a {@link LongNode} or a {@link BigIntegerNode} depending on
 * the precision.</p>
 */
public final class CustomJsonNodeFactory
    extends JsonNodeFactory
{
    private static final JsonNodeFactory INSTANCE = new CustomJsonNodeFactory();

    public static JsonNodeFactory getInstance()
    {
        return INSTANCE;
    }

    private CustomJsonNodeFactory()
    {
    }

    @Override
    public NumericNode numberNode(final BigDecimal v)
    {
        /*
         * 0 is a particular case for BigDecimal: even new BigDecimal("0") has
         * scale 1. We have to special case it.
         */
        if (v.compareTo(BigDecimal.ZERO) == 0)
            return IntNode.valueOf(0);

        /*
         * Strip decimals. For anything other than 0, if there are no fraction
         * digits or negative exponents, the scale will be 0, therefore it will
         * be an integer value.
         */
        final BigDecimal decimal = v.stripTrailingZeros();
        if (decimal.scale() != 0)
            return DecimalNode.valueOf(decimal);

        /*
         * In which case we take the underlying BigInteger value...
         */
        final BigInteger value = decimal.toBigInteger();

        /*
         * And act according to its bit length.
         */
        final int relSize = value.bitLength() / 32;

        switch (relSize) {
            case 0:
                return IntNode.valueOf(value.intValue());
            case 1:
                return LongNode.valueOf(value.longValue());
            default:
                return new BigIntegerNode(value);
        }
    }
}
