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

package eel.kitchen.jsonschema.v2.validation;

import eel.kitchen.jsonschema.v2.validation.base.Validator;
import org.codehaus.jackson.JsonNode;

public final class JsonValidator
{
    private final ValidatorFactory factory;

    public JsonValidator(final JsonNode schema)
    {
        factory = new ValidatorFactory(schema);
    }

    public ValidationReport validate(final JsonNode instance)
    {
        final Validator validator = factory.getValidator(instance);
        return validator.validate();
    }
}
