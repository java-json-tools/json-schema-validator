/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.main;


import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.JsonPointer;

/**
 * The main interface to use for JSON Schema validation
 *
 * <p>Apart from validating JSON instances, it has several other roles:</p>
 * <ul>
 *     <li>determining which JSON Schema version is used by default,
 *     if schemas do not provide a {@code $schema} keyword;</li>
 *     <li>registering/unregistering validators against a particular schema
 *     version;</li>
 *     <li>registering/unregistering a {@link URIHandler} for a particular
 *     scheme.
 *     </li>
 * </ul>
 *
 * @see JsonLoader
 * @see ValidationContext
 */
public final class JsonValidator
{

    /**
     * This validator's {@link ValidationContext}
     */
    private final ValidationContext context;

    /**
     * The constructor
     *
     * @param cfg the {@link ValidationConfig} object
     * @param schema the root schema to use for validation
     * @throws JsonValidationFailureException the initial JSON node is not a
     * schema
     */
    public JsonValidator(final ValidationConfig cfg, final JsonNode schema)
        throws JsonValidationFailureException
    {
        cfg.buildFactories();

        final SchemaProvider provider = new SchemaProvider(cfg, schema);
        context = new ValidationContext(cfg, provider);
    }

    /**
     * Validate an instance against the schema
     *
     * @param instance the instance to validate
     * @return the validation report
     * @throws JsonValidationFailureException on validation failure,
     * if {@link ValidationFeature#FAIL_FAST} is set
     */
    public ValidationReport validate(final JsonNode instance)
        throws JsonValidationFailureException
    {
        final Validator validator = context.getValidator(instance);
        return validator.validate(context, instance);
    }

    /**
     * Validate an instance against a subschema of a given schema
     *
     * <p>If, for instance, you have a JSON document such as:</p>
     * <pre>
     *     {
     *         "schema1": { "some": "schema here" },
     *         "schema2": { "another": "schema here" }
     *     }
     * </pre>
     * <p>then you will be able to validate instances against {@code
     * schema1} by invoking this method with {@code #/schema1} as the path</p>
     *
     * @param path the path to the actual schema
     * @param instance the instance to validate
     * @return a report of the validation
     * @throws JsonValidationFailureException on validation failure,
     * if {@link ValidationFeature#FAIL_FAST} is set
     */
    public ValidationReport validate(final String path, final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonPointer pointer = new JsonPointer(path);

        final Validator validator = context.getValidator(pointer, instance);
        return validator.validate(context, instance);
    }

    /**
     * Validate the registered schema
     *
     * @return a validation report
     * @throws JsonValidationFailureException if reporting has been set to
     * throw this exception instead of collecting messages
     */
    public ValidationReport validateSchema()
        throws JsonValidationFailureException
    {
        return context.validateSchema();
    }
}
