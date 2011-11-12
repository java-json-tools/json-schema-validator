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

package org.eel.kitchen.jsonschema.container;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.AbstractValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;

/**
 * <p>>A specialized {@link Validator} implementation for validating container
 * nodes (ie, array or object JSON instances).</p>
 *
 * <p>The particularity of these validators is that not only do they need to
 * validate the structure of the instance itself, they must also,
 * if the structure is valid, spawn validators for all the subnodes of the
 * object instance.
 * </p>
 *
 * @see ArrayValidator
 * @see ObjectValidator
 */
public abstract class ContainerValidator
    extends AbstractValidator
{
    /**
     * An empty schema, always true
     */
    protected static final JsonNode EMPTY_SCHEMA
        = JsonNodeFactory.instance.objectNode();

    /**
     * The {@link Validator} which validates the structure of the instance
     * itself
     */
    private final Validator validator;

    protected final ValidationContext context;

    protected final JsonNode instance;

    protected final ValidationReport report;

    /**
     * Constructor
     *
     * @param validator the structure validator, see {@link #validator}
     * @param context the {@link ValidationContext} to use
     * @param instance the instance to validate
     */
    protected ContainerValidator(final Validator validator,
        final ValidationContext context, final JsonNode instance)
    {
        this.validator = validator;
        this.context = context;
        this.instance = instance;
        report = context.createReport();
    }

    /**
     * Method used to build the necessary structures to provide validators
     * for subnodes. Used before calling {@link #getValidator(String,
     * JsonNode)}.
     */
    protected abstract void buildPathProvider();

    /**
     * Provide a {@link Validator} for a subnode, according to its path
     *
     * @param path the path of the child node
     * @param child the child node
     * @return the matching validator
     */
    protected abstract Validator getValidator(final String path,
        final JsonNode child);

    /**
     * Validate all children nodes, in the event that structure validation
     * succeeds
     */
    protected abstract void validateChildren();

    /**
     * Validate the instance. First, validates the structure of the instance
     * itself, using {@link #validator}, then, if successful,
     * builds the necessary element to provide children validators (using
     * {@link #buildPathProvider()} and then {@link #validateChildren()}.
     *
     * @return the validation report
     */
    @Override
    public final ValidationReport validate()
    {
        report.mergeWith(validator.validate());

        if (!report.isSuccess())
            return report;

        buildPathProvider();
        validateChildren();

        return report;
    }
}
