/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;

import java.util.Set;

/**
 * The main validator
 *
 * <p>Such a validator is only called when the schema syntax has been verified
 * to be correct. It is also responsible to instantiate an {@link
 * ArrayValidator} or {@link ObjectValidator} if necessary.</p>
 *
 * @see JsonValidatorCache#getValidator(SchemaNode)
 */
final class InstanceValidator
    implements JsonValidator
{
    /**
     * The schema node
     */
    private final SchemaNode schemaNode;

    /**
     * The set of keyword validators for that schema node
     */
    private final Set<KeywordValidator> validators;

    /**
     * Constructor, package private
     *
     * @param schemaNode the schema node
     * @param validators the set of keyword validators
     */
    InstanceValidator(final SchemaNode schemaNode,
        final Set<KeywordValidator> validators)
    {
        this.validators = ImmutableSet.copyOf(validators);
        this.schemaNode = schemaNode;
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final SchemaContainer orig = context.getContainer();
        context.setContainer(schemaNode.getContainer());

        for (final KeywordValidator validator: validators) {
            validator.validateInstance(context, report, instance);
            if (report.hasFatalError())
                return;
        }

        /*
         * Check now whether the instance is a container with at least one
         * element. Jackson's .size() works for value nodes as well and always
         * returns 0 in this case, so this is safe to to.
         *
         * For containers, however, we don't bother testing their children if
         * the container itself is invalid: check whether the report is a
         * success before we proceed.
         */
        if (instance.size() > 0 && report.isSuccess()) {
            final JsonValidator validator = instance.isArray()
                ? new ArrayValidator(schemaNode.getNode())
                : new ObjectValidator(schemaNode.getNode());

            validator.validate(context, report, instance);
        }
        context.setContainer(orig);
    }
}
