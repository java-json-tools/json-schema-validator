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
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.List;

abstract class UnsupportedSyntaxValidator
    implements SyntaxValidator
{
    final String message;

    protected UnsupportedSyntaxValidator(final String fieldName)
    {
        message = String.format("Sorry, %s not implemented yet", fieldName);
    }

    @Override
    public final void validate(final ValidationState state,
        final JsonNode schema)
    {
        state.addMessage(message);
        state.setStatus(ValidationStatus.FAILURE);
    }
}
