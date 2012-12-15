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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ValueNode;

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
    private static final JsonNodeFactory INSTANCE
        = new CustomJsonNodeFactory();

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
    public NumericNode numberNode(final byte v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public ValueNode numberNode(final Byte value)
    {
        return new NumberNode(super.numberNode(value));
    }

    @Override
    public NumericNode numberNode(final short v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public ValueNode numberNode(final Short value)
    {
        return new NumberNode(super.numberNode(value));
    }

    @Override
    public NumericNode numberNode(final int v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public ValueNode numberNode(final Integer value)
    {
        return new NumberNode(super.numberNode(value));
    }

    @Override
    public NumericNode numberNode(final long v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public ValueNode numberNode(final Long value)
    {
        return new NumberNode(super.numberNode(value));
    }

    @Override
    public NumericNode numberNode(final BigInteger v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public NumericNode numberNode(final float v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public ValueNode numberNode(final Float value)
    {
        return new NumberNode(super.numberNode(value));
    }

    @Override
    public NumericNode numberNode(final double v)
    {
        return new NumberNode(super.numberNode(v));
    }

    @Override
    public ValueNode numberNode(final Double value)
    {
        return new NumberNode(super.numberNode(value));
    }

    @Override
    public NumericNode numberNode(final BigDecimal v)
    {
        return new NumberNode(super.numberNode(v));
    }
}
