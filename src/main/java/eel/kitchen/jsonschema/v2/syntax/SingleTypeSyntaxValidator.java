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

import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;
import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

abstract class SingleTypeSyntaxValidator
    implements SyntaxValidator
{
    protected final String fieldName;

    private final NodeType expected;

    protected SingleTypeSyntaxValidator(final String fieldName,
        final NodeType expected)
    {
        this.fieldName = fieldName;
        this.expected = expected;
    }

    @Override
    public void validate(final ValidationState state, final JsonNode schema)
    {
        final NodeType actual = NodeType.getNodeType(schema.get(fieldName));

        if (expected == actual) {
            state.setStatus(ValidationStatus.SUCCESS);
            return;
        }

        state.addMessage(String
            .format("illegal type for field %s: is %s, expected %s",
                fieldName, actual, expected));
        state.setStatus(ValidationStatus.FAILURE);
    }
}
