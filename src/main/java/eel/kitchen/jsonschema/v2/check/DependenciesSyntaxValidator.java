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

package eel.kitchen.jsonschema.v2.check;

import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public final class DependenciesSyntaxValidator
    extends SingleTypeSyntaxValidator
{
    public DependenciesSyntaxValidator()
    {
        super("dependencies", NodeType.OBJECT);
    }

    @Override
    public void validate(final ValidationState state, final JsonNode schema)
    {
        super.validate(state, schema);

        if (state.isFailure())
            return;

        for (final JsonNode element: schema.get(fieldName)) {
            if (element.isTextual())
                continue;
            if (element.isObject())
                continue;
            if (element.isArray()) {
                for (final JsonNode subNode: element)
                    if (!element.isTextual()) {
                        state.addMessage("Non string dependency in "
                            + "dependency array");
                        state.setStatus(ValidationStatus.FAILURE);
                        return;
                    }
            }
        }
    }
}
