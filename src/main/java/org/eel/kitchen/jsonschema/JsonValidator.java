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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema;


import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.NodeType;

/**
 * The main interface to use for JSON Schema validation
 *
 * @see JsonLoader
 * @see ValidationContext
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

    // TODO: javadoc, and decide where to handle errors -- probably here
    public void unregisterValidator(final String keyword)
    {
        context.unregisterValidator(keyword);
    }

    public void registerValidator(final String keyword,
        final Class<? extends SyntaxValidator> sv,
        final Class<? extends KeywordValidator> kv, final NodeType... types)
    {
        context.registerValidator(keyword, sv, kv, types);
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

    /**
     * Validate an instance against a subschema of a given schema
     *
     * <p>If, for instance, you have a schema defined as:</p>
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
     */
    public ValidationReport validate(final String path, final JsonNode instance)
    {
        final String realPath = path.replaceFirst("#", "");

        final Validator validator = context.getValidator(realPath, instance);
        return validator.validate();
    }
}
