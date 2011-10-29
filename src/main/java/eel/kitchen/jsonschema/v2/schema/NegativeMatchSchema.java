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

package eel.kitchen.jsonschema.v2.schema;

import eel.kitchen.jsonschema.v2.instance.Instance;
import eel.kitchen.jsonschema.v2.keyword.ValidationStatus;

public final class NegativeMatchSchema
    extends AbstractSchema
{
    private final Schema schema;

    public NegativeMatchSchema(final Schema schema)
    {
        this.schema = schema;
    }

    @Override
    public Schema getSchema(final String path)
    {
        return schema.getSchema(path);
    }

    @Override
    public void validate(final ValidationState state, final Instance instance)
    {
        final ValidationState current = new ValidationState(state);

        schema.validate(current, instance);

        if (current.isFailure()) {
            state.setStatus(ValidationStatus.SUCCESS);
            return;
        }

        state.setStatus(ValidationStatus.FAILURE);
        state.addMessage("instance matches a forbidden schema");
    }

    @Override
    public boolean validate(final Instance instance)
    {
        if (!schema.validate(instance))
            return true;

        messages.add("instance matches a forbidden schema");
        return false;
    }
}
