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

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.math.BigDecimal;

public final class MinimumKeywordValidator
    extends AbstractKeywordValidator
{
    private BigDecimal minimum = null;
    private boolean exclusiveMinimum = false;

    public MinimumKeywordValidator(final JsonNode schema)
    {
        super(schema, NodeType.INTEGER, NodeType.NUMBER);
    }

    @Override
    protected void setup()
    {
        minimum = schema.get("minimum").getDecimalValue();
        exclusiveMinimum = schema.path("exclusiveMinimum").asBoolean(false);
    }

    @Override
    public boolean doValidate(final JsonNode instance)
    {
        final int cmp = minimum.compareTo(instance.getDecimalValue());

        if (cmp > 0) {
            messages.add("instance is lower than the required minimum");
            return false;
        }

        if (cmp == 0 && exclusiveMinimum) {
            messages.add("instance is not strictly greater than the required "
                + "minimum");
            return false;
        }

        return true;
    }
}
