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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Custom {@link JsonNodeFactory} and {@link ObjectMapper}
 *
 * <p>Jackson's default node factory and mapper fall short of conformant JSON
 * Schema processing on two points:</p>
 *
 * <ul>
 *     <li>for floating point instances, {@code double} is used by default;
 *     however, neither the <a href="http://tools.ietf.org/html/rfc4627>JSON
 *     RFC</a> nor the JSON Schema specifications define limits on the scale
 *     and/or precision of numeric values;</li>
 *     <li>again for numeric values, equality is defined by JSON Schema as
 *     mathematical equality, which means for instance {@code 1.0} and {@code 1}
 *     are equal; but the default implementation considers them unequal (since
 *     the first is a floating point number and the other is an integer).</li>
 * </ul>
 *
 * <p>To work around this, this class extends upon both {@link JsonNodeFactory}
 * and {@link ObjectMapper}:</p>
 *
 * <ul>
 *     <li>it uses {@link DeserializationFeature#USE_BIG_DECIMAL_FOR_FLOATS}
 *     when creating the mapper; this allows to not lose any scale or precision
 *     on arbitrarily large floating point instances;</li>
 *     <li>it overrides {@link JsonNodeFactory#numberNode(BigDecimal)} to strip
 *     any final zeroes and generates a (precision-dependent) integer node if
 *     the resulting decimal part is empty.</li>
 * </ul>
 *
 * @see BigDecimal#stripTrailingZeros()
 * @see BigDecimal#scale()
 */
public final class CustomJsonNodeFactory
    extends JsonNodeFactory
{
    private static final JsonNodeFactory INSTANCE = new CustomJsonNodeFactory();

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
        .setNodeFactory(INSTANCE);

    /**
     * Get the only instance of the class
     *
     * @return the factory
     */
    public static JsonNodeFactory getInstance()
    {
        return INSTANCE;
    }

    /**
     * Get the embedded {@link ObjectMapper}
     *
     * @return the mapper
     */
    public static ObjectMapper getMapper()
    {
        return MAPPER;
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
