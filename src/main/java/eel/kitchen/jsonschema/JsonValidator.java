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

package eel.kitchen.jsonschema;

import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.JsonLoader;
import org.codehaus.jackson.JsonNode;

/**
 * The main interface to use for JSON Schema validation
 *
 * @see {@link JsonLoader}
 * @see {@link ValidationContext}
 */
public final class JsonValidator
{
    /**
     * The context, initialized by the constructor
     */
    private final ValidationContext context;

    /**
     * The constructor
     *
     * @param schema the root schema to use for validation
     */
    public JsonValidator(final JsonNode schema)
    {
        context = new ValidationContext(schema);
    }

    /**
     * Validate an instance against the schema
     * @param instance the instance to validate
     * @return the validation report
     */
    public ValidationReport validate(final JsonNode instance)
    {
        final Validator validator = context.getValidator(instance);
        return validator.validate();
    }
}
