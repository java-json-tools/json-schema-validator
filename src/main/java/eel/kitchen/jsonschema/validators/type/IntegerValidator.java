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

import eel.kitchen.jsonschema.validators.AbstractValidator;
import eel.kitchen.jsonschema.validators.format.FormatValidator;
import eel.kitchen.jsonschema.validators.misc.EnumValidator;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.math.BigInteger;

/**
 * <p>Validator for an integer. This covers the following keywords of the spec
 * (the same are used by {@link NumberValidator}, except the latter validates
 * decimal numbers in addition to integers):</p>
 * <ul>
 *     <li>minimum and maximum (5.9 and 5.10);</li>
 *     <li>exclusiveMinimum and exclusiveMaximum (5.11 and 5.12);</li>
 *     <li>divisibleBy (5.24).</li>
 * </ul>
 * <p>As no limit is made to the size of the number,
 * this uses {@link BigInteger} objects to read and validate numbers.</p>
 */
public final class IntegerValidator
    extends AbstractValidator
{
    /**
     * fields corresponding to the different keywords.
     */
    private BigInteger minimum = null, maximum = null, divisor = null;
    private boolean exclusiveMinimum = false, exclusiveMaximum = false;

    public IntegerValidator()
    {
        registerField("minimum", NodeType.INTEGER);
        registerField("maximum", NodeType.INTEGER);
        registerField("divisibleBy", NodeType.INTEGER);
        registerField("exclusiveMinimum", NodeType.BOOLEAN);
        registerField("exclusiveMaximum", NodeType.BOOLEAN);

        registerValidator(new EnumValidator());
        registerValidator(new FormatValidator());
    }

    @Override
    protected void reset()
    {
        minimum = maximum = divisor = null;
        exclusiveMinimum = exclusiveMaximum = false;
    }

    /**
     * <p>Validates the schema. Causes for validation failures are many:</p>
     * <ul>
     *     <li>minimum is greater than maximum;</li>
     *     <li>minimum and maximum are equal, but one of exclusive* is set,
     *     effectively disallowing this schema to validate anything;</li>
     *     <li>exclusiveM{in,ax}imum without m{in,ax}imum;</li>
     *     <li>divisibleBy is 0.</li>
     * </ul>
     * <p>The first three points are checked by <code>checkMinMax()</code>.
     * </p>
     *
     * @return false if one of the conditions above is met
     */
    @Override
    protected boolean doSetup()
    {
        if (!checkMinMax())
            return false;

        final JsonNode node = schema.get("divisibleBy");

        if (node == null)
            return true;

        divisor = node.getBigIntegerValue();

        if (!BigInteger.ZERO.equals(divisor))
            return true;

        schemaErrors.add("divisibleBy is 0");
        return false;
    }

    /**
     * Check coherency of minimum, maximum, exclusiveMinimum and
     * exclusiveMaximum as described in <code>doSetup()</code>.
     *
     * @return true if these values are coherent, false otherwise
     */
    private boolean checkMinMax()
    {
        JsonNode node;

        node = schema.path("minimum");

        if (node.isIntegralNumber())
            minimum = node.getBigIntegerValue();

        node = schema.get("exclusiveMinimum");

        if (node != null) {
            if (minimum == null) {
                schemaErrors.add("exclusiveMinimum without minimum");
                return false;
            }
            exclusiveMinimum = node.getBooleanValue();
        }

        node = schema.path("maximum");

        if (node.isIntegralNumber())
            maximum = node.getBigIntegerValue();

        node = schema.get("exclusiveMaximum");

        if (node != null) {
            if (maximum == null) {
                schemaErrors.add("exclusiveMaximum without maximum");
                return false;
            }
            exclusiveMaximum = node.getBooleanValue();
        }

        if (minimum != null && maximum != null) {
            final int tmp = minimum.compareTo(maximum);
            if (tmp > 0) {
                schemaErrors.add("minimum is greater than maximum");
                return false;
            }
            if (tmp == 0 && (exclusiveMinimum || exclusiveMaximum)) {
                schemaErrors.add("schema can never validate: minimum and maximum "
                    + "are equal but are excluded from matching");
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
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

        if (BigInteger.ZERO.equals(value.remainder(divisor)))
            return true;

        messages.add("integer is not a multiple of the declared divisor");
        return false;
    }
}
