/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.type;

import eel.kitchen.jsonschema.exception.MalformedJasonSchemaException;
import eel.kitchen.jsonschema.validators.AbstractValidator;
import org.codehaus.jackson.JsonNode;

import java.math.BigInteger;

public final class IntegerValidator
    extends AbstractValidator
{
    private static final BigInteger ZERO = new BigInteger("0");

    private BigInteger minimum = null, maximum = null, divisor = null;
    private boolean exclusiveMinimum = false, exclusiveMaximum = false;

    public IntegerValidator(final JsonNode schema)
    {
        super(schema);
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        JsonNode node;

        node = schema.get("minimum");

        if (node != null) {
            if (!node.isIntegralNumber())
                throw new MalformedJasonSchemaException("minimum is not an " +
                    "integer");
            minimum = node.getBigIntegerValue();
        }

        node = schema.get("exclusiveMinimum");

        if (node != null) {
            if (!node.isBoolean())
                throw new MalformedJasonSchemaException("exclusiveMinimum is " +
                    "not a boolean");
            exclusiveMinimum = node.getBooleanValue();
        }

        node = schema.get("maximum");

        if (node != null) {
            if (!node.isIntegralNumber())
                throw new MalformedJasonSchemaException("maximum is not an " +
                    "integer");
            maximum = node.getBigIntegerValue();
        }

        node = schema.get("exclusiveMaximum");

        if (node != null) {
            if (!node.isBoolean())
                throw new MalformedJasonSchemaException("exclusiveMaximum is " +
                    "not a boolean");
            exclusiveMaximum = node.getBooleanValue();
        }

        if (minimum != null && maximum != null) {
            final int tmp = minimum.compareTo(maximum);
            if (tmp > 0)
                throw new MalformedJasonSchemaException("minimum should be " +
                    "less than or equal to maximum");
            if (tmp == 0 && (exclusiveMinimum || exclusiveMaximum))
                throw new MalformedJasonSchemaException("schema can never " +
                    "validate: minimum equals maximum, but one, or both, " +
                    "is excluded from matching");
        }

        node = schema.get("divisibleBy");

        if (node != null) {
            if (!node.isIntegralNumber())
                throw new MalformedJasonSchemaException("divisibleBy is not " +
                    "an integer");
            divisor = node.getBigIntegerValue();
            if (divisor.compareTo(ZERO) == 0)
                throw new MalformedJasonSchemaException("divisibleBy cannot " +
                    "be zero");
        }
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final BigInteger value = node.getBigIntegerValue();

        int tmp;

        if (minimum != null) {
            tmp = value.compareTo(minimum);
            if (tmp < 0) {
                messages.add("integer is strictly lower than the "
                    + "required minimum");
                return false;
            }
            if (tmp == 0 && exclusiveMinimum) {
                messages.add("integer equals to the minimum, "
                    + "but should be strictly greater than it");
                return false;
            }
        }

        if (maximum != null) {
            tmp = value.compareTo(maximum);
            if (tmp > 0) {
                messages.add("integer is strictly greater than the "
                    + "required maximum");
                return false;
            }
            if (tmp == 0 && exclusiveMaximum) {
                messages.add("integer equals to the maximum, "
                    + "but should be strictly lower than it");
                return false;
            }
        }

        if (divisor == null)
            return true;

        if (ZERO.equals(value.remainder(divisor)))
            return true;

        messages.add("integer is not a multiple of the declared divisor");
        return false;
    }
}
