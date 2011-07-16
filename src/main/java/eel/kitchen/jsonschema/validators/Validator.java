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

package eel.kitchen.jsonschema.validators;

/**
 * <p>Interface defining an individual JSON schema validator.</p>
 *
 * <p>Validators can validate whole structures (arrays, objects),
 * but also non container values (strings), or even particular properties in
 * the schema (e.g. "format", "dependencies").
 * </p>
 */

import eel.kitchen.util.IterableJsonNode;
import org.codehaus.jackson.JsonNode;

import java.util.List;

public interface Validator
{
    /**
     * <p>Set a schema for this validator. By default, if you extend
     * {@link AbstractValidator} (recommended), this will be the empty
     * schema ({}), which means the validator will always succeed if this
     * method is not called.</p>
     *
     * <p>Validators are reusable, so you can use the same validator instance
     * to validate against several schemas, ie call this method more than
     * once.</p>
     * @param schema The schema that this validator should use
     * @return this, for method chaining (with .setup() tipycally)
     */
    Validator setSchema(final JsonNode schema);

    /**
     * <p>Check that the provided schema is valid. You SHOULD call this
     * method before calling .validate(). If you extend {@link
     * AbstractValidator}, it does it for you.
     * </p>
     *
     * @return true if the provided schema is valid
     */
    boolean setup();

    /**
     * <p>Validate one JSON instance against this Validator.</p>
     *
     * @param node The instance to validate
     * @return true if the instance is valid
     */
    boolean validate(final JsonNode node);

    /**
     * <p>Return validation messages. By contract, the list returned MUST be
     * empty if validation succeeds, and MUST be reset on each call to
     * .validate().
     * </p>
     *
     * @return the validation messages
     */
    List<String> getMessages();

    /**
     * <p>Return the schema provider for this Validator. When validating
     * arrays and objects, for instance, apart from validating the array and
     * object itself, individual elements must also be validated. The {@link
     * SchemaProvider} instance returned by this method gives access to the
     * schemas associated to these individual elements, by path. For other,
     * non container elements, returns an empty provider.
     * </p>
     *
     * @see {@link SchemaProvider}, {@link IterableJsonNode}
     *
     * @return the SchemaProvider for this validator
     */
    SchemaProvider getSchemaProvider();
}
