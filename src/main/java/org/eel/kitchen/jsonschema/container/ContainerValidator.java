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
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

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
abstract class ContainerValidator
    implements Validator
{
    /**
     * The {@link Validator} which validates the structure of the instance
     */
    private final Validator validator;

    /**
     * Constructor
     *
     * @param validator the structure validator, see {@link #validator}
     */
    ContainerValidator(final Validator validator)
    {
        this.validator = validator;
    }

    /**
     * Validate the children node of the instance
     *
     * @param context the context
     * @param instance the instance
     * @return the report
     * @throws JsonValidationFailureException if the report is set to throw it
     */
    protected abstract ValidationReport validateChildren(
        final ValidationContext context, final JsonNode instance)
        throws JsonValidationFailureException;

    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();
        report.mergeWith(validator.validate(context, instance));

        if (!report.isSuccess())
            return report;

        report.mergeWith(validateChildren(context, instance));

        return report;
    }
}
