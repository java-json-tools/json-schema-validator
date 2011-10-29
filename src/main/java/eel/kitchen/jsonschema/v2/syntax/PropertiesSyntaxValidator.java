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

package eel.kitchen.jsonschema.v2.syntax;

import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public final class PropertiesSyntaxValidator
    extends SingleTypeSyntaxValidator
{
    public PropertiesSyntaxValidator(final String fieldName)
    {
        super("properties", NodeType.OBJECT);
    }

    @Override
    public void validate(final ValidationState state, final JsonNode schema)
    {
        super.validate(state, schema);

        if (state.isFailure())
            return;

        for (final JsonNode element: schema.get(fieldName))
            validateOne(state, element);
    }

    private static void validateOne(final ValidationState state,
        final JsonNode element)
    {
        if (!element.isObject()) {
            state.addMessage("non schema value in properties");
            return;
        }

        if (!element.has("required"))
            return;

        if (element.get("required").isBoolean())
            return;

        state.addMessage("required attribute of schema in properties is not a"
            + " boolean");
    }
}
