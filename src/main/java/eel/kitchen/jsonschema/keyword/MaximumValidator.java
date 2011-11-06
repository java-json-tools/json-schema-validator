/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.context.ValidationContext;
import org.codehaus.jackson.JsonNode;

import java.math.BigDecimal;

public final class MaximumValidator
    extends KeywordValidator
{
    private final BigDecimal maximum;
    private final boolean exclusiveMaximum;

    public MaximumValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
        maximum = schema.get("maximum").getDecimalValue();
        exclusiveMaximum = schema.path("exclusiveMaximum").asBoolean(false);

    }

    @Override
    protected void validateInstance()
    {
        final int cmp = maximum.compareTo(instance.getDecimalValue());

        if (cmp < 0) {
            report.addMessage("number is greater than the required maximum");
            return;
        }

        if (cmp == 0 && exclusiveMaximum)
            report.addMessage("number is not strictly lower than "
                + "the required maximum");
    }
}
