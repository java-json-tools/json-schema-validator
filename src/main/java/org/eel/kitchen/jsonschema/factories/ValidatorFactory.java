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

package org.eel.kitchen.jsonschema.factories;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.AlwaysTrueValidator;
import org.eel.kitchen.jsonschema.base.MatchAllValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.container.ArrayValidator;
import org.eel.kitchen.jsonschema.container.ObjectValidator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.keyword.common.format.FormatValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.util.Collection;

/**
 * Factory initializing all validator factories with a given schema bundle,
 * and in charge of validator caching
 *
 * @see KeywordFactory
 * @see SyntaxFactory
 * @see FormatFactory
 * @see ValidatorBundle
 */
public abstract class ValidatorFactory
{
    /**
     * The {@link KeywordValidator} factory
     */
    protected final KeywordFactory keywordFactory;

    /**
     * The {@link FormatValidator} factory
     */
    protected final FormatFactory formatFactory = new FormatFactory();

    /**
     * Our validator cache
     */
    private final ValidatorCache cache = new ValidatorCache();

    /**
     * Constructor
     *
     * @param bundle the validator bundle to use
     */
    protected ValidatorFactory(final ValidatorBundle bundle)
    {
        keywordFactory = new KeywordFactory(bundle);
    }

    public abstract ValidationReport validateSchema(
        final ValidationContext context)
        throws JsonValidationFailureException;

    /**
     * Return a {@link KeywordValidator} to validate an instance against a
     * given schema
     *
     * @param context the context containing the schema
     * @param instance the instance to validate
     * @return the matching validator
     */
    public final Validator getInstanceValidator(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode schema = context.getSchema();
        final NodeType type = NodeType.getNodeType(instance);

        Validator ret = cache.get(type, schema);

        if (ret != null)
            return ret;

        final Validator validator;
        final Collection<Validator> collection
            = keywordFactory.getValidators(context, instance);

        switch (collection.size()) {
            case 0:
                validator = new AlwaysTrueValidator();
                break;
            case 1:
                validator = collection.iterator().next();
                break;
            default:
                validator = new MatchAllValidator(collection);
        }

        switch (type) {
            case ARRAY:
                ret = new ArrayValidator(schema, validator);
                break;
            case OBJECT:
                ret = new ObjectValidator(schema, validator);
                break;
            default:
                ret = validator;
        }

        cache.put(type, schema, ret);

        return ret;
    }

    /**
     * Get a validator for a given format specification,
     * context and instance to validate
     *
     * @param context the context
     * @param fmt the format specification
     * @param instance the instance to validate
     * @return the matching {@link FormatValidator}
     * @throws JsonValidationFailureException on validation failure,
     * with the appropriate validation mode
     */
    public final Validator getFormatValidator(final ValidationContext context,
        final String fmt, final JsonNode instance)
        throws JsonValidationFailureException
    {
        return formatFactory.getFormatValidator(context, fmt, instance);
    }
}
