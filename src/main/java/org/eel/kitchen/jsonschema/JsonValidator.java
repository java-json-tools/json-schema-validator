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
import org.eel.kitchen.jsonschema.uri.URIHandler;
import org.eel.kitchen.jsonschema.uri.URIHandlerFactory;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.JsonPointer;
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

    /**
     * Unregister validators for a particular keyword
     *
     * <p>This will unregister both the {@link SyntaxValidator} and {@link
     * KeywordValidator} for this keyword. Note that calling this method
     * will effectively render the keyword <b>unrecognized</b>,
     * which means schemas bearing this particular keyword will be considered
     * <b>INVALID</b>. This is why this method should always be called
     * before, and paired with,
     * {@link #registerValidator(String, Class, Class, NodeType...)},
     * unless you really mean to reduce the subset of recognized keywords.</p>
     *
     * @param keyword the keyword to unregister
     * @throws IllegalArgumentException if keyword is null
     */
    public void unregisterValidator(final String keyword)
    {
        if (keyword == null)
            throw new IllegalArgumentException("keyword is null");

        context.unregisterValidator(keyword);
    }

    /**
     * Register a new set of validators for a particular keyword
     *
     * <p>Note that if {@code null} is passed to validators,
     * then validation will always succeed. Be particularly careful if you
     * pass null as an argument to the syntax validator and not the keyword
     * validator, as the primary role of a syntax validator is to ensure that
     * the keyword validator have the data it expects in the schema!</p>
     *
     * @param keyword the keyword to register
     * @param sv the {@link SyntaxValidator} to register for this keyword
     * @param kv the {@link KeywordValidator} to register for this keyword
     * @param types the list of primitive types the keyword validator applies to
     * @throws IllegalArgumentException if keyword is null
     */
    public void registerValidator(final String keyword,
        final Class<? extends SyntaxValidator> sv,
        final Class<? extends KeywordValidator> kv, final NodeType... types)
    {
        if (keyword == null)
            throw new IllegalArgumentException("keyword is null");

        context.registerValidator(keyword, sv, kv, types);
    }

    /**
     * Register a new {@link URIHandler} for a given scheme
     *
     * @param scheme the scheme
     * @param handler the handler
     * @throws IllegalArgumentException the provided scheme is null
     *
     * @see URIHandlerFactory#registerHandler(String, URIHandler)
     */
    public void registerURIHandler(final String scheme, final URIHandler handler)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        context.registerURIHandler(scheme, handler);
    }

    /**
     * Unregister the handler for a given scheme
     *
     * @param scheme the victim
     * @throws IllegalArgumentException the provided scheme is null
     *
     * @see URIHandlerFactory#unregisterHandler(String)
     */
    public void unregisterURIHandler(final String scheme)
    {
        if (scheme == null)
            throw new IllegalArgumentException("scheme is null");

        context.unregisterURIHandler(scheme);
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
        final JsonPointer pointer = new JsonPointer(path);

        final Validator validator = context.getValidator(pointer, instance);
        return validator.validate();
    }
}
