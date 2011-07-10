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
import org.codehaus.jackson.JsonNode;

import java.math.BigDecimal;

public final class NumberValidator
    extends AbstractTypeValidator
{
    private static final BigDecimal ZERO = new BigDecimal("0");

    private BigDecimal minimum = null, maximum = null, divisor = null;
    private boolean exclusiveMinimum = false, exclusiveMaximum = false;

    public NumberValidator(final JsonNode schemaNode)
    {
        super(schemaNode);
    }

    @Override
    public void setup()
        throws MalformedJasonSchemaException
    {
        JsonNode node;

        node = schemaNode.get("minimum");

        if (node != null) {
            if (!node.isNumber())
                throw new MalformedJasonSchemaException("minimum is not a " +
                    "number");
            minimum = node.getDecimalValue();
        }

        node = schemaNode.get("exclusiveMinimum");

        if (node != null) {
            if (!node.isBoolean())
                throw new MalformedJasonSchemaException("exclusiveMinimum is " +
                    "not a boolean");
            exclusiveMinimum = node.getBooleanValue();
        }

        node = schemaNode.get("maximum");

        if (node != null) {
            if (!node.isNumber())
                throw new MalformedJasonSchemaException("maximum is not a " +
                    "number");
            maximum = node.getDecimalValue();
        }

        node = schemaNode.get("exclusiveMaximum");

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

        node = schemaNode.get("divisibleBy");

        if (node != null) {
            if (!node.isNumber())
                throw new MalformedJasonSchemaException("divisibleBy is not " +
                    "a number");
            divisor = node.getDecimalValue();
            if (divisor.compareTo(ZERO) == 0)
                throw new MalformedJasonSchemaException("divisibleBy cannot " +
                    "be zero");
        }
    }

    @Override
    public boolean validate(final JsonNode node)
    {
        final BigDecimal value = node.getDecimalValue();

        int tmp;

        if (minimum != null) {
            tmp = value.compareTo(minimum);
            if (tmp < 0) {
                validationErrors.add("number is strictly lower than the "
                    + "required minimum");
                return false;
            }
            if (tmp == 0 && exclusiveMinimum) {
                validationErrors.add("number equals to the minimum, "
                    + "but should be strictly greater than it");
                return false;
            }
        }

        if (maximum != null) {
            tmp = value.compareTo(maximum);
            if (tmp > 0) {
                validationErrors.add("number is strictly greater than the "
                    + "required maximum");
                return false;
            }
            if (tmp == 0 && exclusiveMaximum) {
                validationErrors.add("number equals to the maximum, "
                    + "but should be strictly lower than it");
                return false;
            }
        }

        if (divisor == null)
            return true;

        if (ZERO.compareTo(value.remainder(divisor)) == 0)
            return true;

        validationErrors.add("number is not a multiple of the declared divisor");
        return false;
    }
}
