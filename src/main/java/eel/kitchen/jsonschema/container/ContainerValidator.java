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

package eel.kitchen.jsonschema.container;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.AbstractValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;

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
    extends KeywordValidator
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
        super(context, instance);
        this.validator = validator;
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
     * Build the validation queue, in the event that structure validation
     * succeeds
i    *
     * @see AbstractValidator#queue
     */
    protected abstract void buildQueue();

    /**
     * Validate the instance. First, validates the structure of the instance
     * itself, using {@link #validator}, then, if successful,
     * builds the necessary element to provide children validators (using
     * {@link #buildPathProvider()} and then {@link #buildQueue()}, and
     * validates them all.
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
        buildQueue();

        while (hasMoreElements())
            report.mergeWith(nextElement().validate());

        queue.clear();
        return report;
    }
}
