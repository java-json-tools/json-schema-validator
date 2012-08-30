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
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.report.ValidationReport;

import java.util.Set;

/**
 * The main validator
 *
 * <p>Such a validator is only called when the schema syntax has been verified
 * to be correct. It is also responsible to instantiate an {@link
 * ArrayValidator} or {@link ObjectValidator} if necessary.</p>
 *
 * @see JsonValidatorCache#getValidator(SchemaNode)
 * @see KeywordValidator
 */
final class InstanceValidator
    implements JsonValidator
{
    private final JsonValidatorCache cache;
    private final SchemaNode schemaNode;
    private final Set<KeywordValidator> validators;

    /**
     * Constructor, package private
     *
     * @param cache the JSON validator cache
     * @param schemaNode the schema node
     * @param validators the set of keyword validators
     */
    InstanceValidator(final JsonValidatorCache cache,
        final SchemaNode schemaNode, final Set<KeywordValidator> validators)
    {
        this.validators = ImmutableSet.copyOf(validators);
        this.schemaNode = schemaNode;
        this.cache = cache;
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final SchemaContainer orig = context.getContainer();

        for (final KeywordValidator validator: validators) {
            context.setContainer(schemaNode.getContainer());
            validator.validateInstance(context, report, instance);
        }

        context.setContainer(orig);

        if (!instance.isContainerNode())
            return false;

        final JsonValidator validator = instance.isArray()
            ? new ArrayValidator(cache, schemaNode)
            : new ObjectValidator(cache, schemaNode);

        validator.validate(context, report, instance);
        return false;
    }
}
