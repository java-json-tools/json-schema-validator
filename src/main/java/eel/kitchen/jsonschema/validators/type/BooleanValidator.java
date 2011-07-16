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
import eel.kitchen.jsonschema.validators.misc.EnumValidator;
import org.codehaus.jackson.JsonNode;

/**
 * Validate against a boolean value. This validator by itself pretty much
 * always returns true. It will only return false if the registered {@link
 * EnumValidator} constrains the boolean to be true or false and the value
 * isn't that.
 */
public final class BooleanValidator
    extends AbstractValidator
{
    public BooleanValidator()
    {
        registerValidator(new EnumValidator());
    }

    @Override
    protected void reset()
    {
    }

    @Override
    protected boolean doSetup()
    {
        return true;
    }

    @Override
    protected boolean doValidate(final JsonNode node)
    {
        return true;
    }
}
