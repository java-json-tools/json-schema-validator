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

package eel.kitchen.jsonschema.v2.validation.syntax;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public final class MinItemsSyntaxValidator
    extends SyntaxValidator
{
    public MinItemsSyntaxValidator(final JsonNode schemaNode)
    {
        super(schemaNode, "minItems", NodeType.INTEGER);
    }

    @Override
    protected void checkFurther()
    {
        if (!node.isInt()) {
            report.addMessage("minItems is too large");
            return;
        }

        if (node.getIntValue() < 0)
            report.addMessage("minItems is negative");
    }
}
