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

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.ValidationState;
import org.codehaus.jackson.JsonNode;

import java.math.BigDecimal;

public final class MinimumKeywordValidator
    extends TrueFalseKeywordValidator
{
    private final BigDecimal minimum;
    private final boolean exclusiveMinimum;

    public MinimumKeywordValidator(final JsonNode schema)
    {
        super(schema);
        minimum = schema.get("minimum").getDecimalValue();
        exclusiveMinimum = schema.path("exclusiveMinimum").asBoolean(false);
    }

    @Override
    public void validate(final ValidationState state, final JsonNode node)
    {
        final int cmp = minimum.compareTo(node.getDecimalValue());

        if (cmp > 0) {
            state.addMessage("instance is lower than the required minimum");
            state.setStatus(ValidationStatus.FAILURE);
            return;
        }

        if (cmp == 0 && exclusiveMinimum) {
            state.addMessage("instance is not strictly greater than the "
                + "required minimum");
            state.setStatus(ValidationStatus.FAILURE);
            return;
        }

        state.setStatus(ValidationStatus.SUCCESS);
    }

    @Override
    public ValidationStatus validate(final JsonNode node)
    {
        final int cmp = minimum.compareTo(node.getDecimalValue());

        if (cmp > 0) {
            messages.add("instance is lower than the required minimum");
            return ValidationStatus.FAILURE;
        }

        if (cmp == 0 && exclusiveMinimum) {
            messages.add("instance is not strictly greater than the required "
                + "minimum");
            return ValidationStatus.FAILURE;
        }

        return ValidationStatus.SUCCESS;
    }
}
