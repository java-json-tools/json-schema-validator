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

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.main.FullValidationReport;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory initializing all validator factories with a given schema bundle,
 * and in charge of validator caching
 *
 * @see KeywordFactory
 * @see SyntaxFactory
 * @see ValidatorBundle
 */
public final class FullValidatorFactory
    extends ValidatorFactory
{
    /**
     * The {@link SyntaxValidator} factory
     */
    private final SyntaxFactory syntaxFactory;

    /**
     * List of already validated schemas
     */
    private final Set<JsonNode> validated = new HashSet<JsonNode>();

    /**
     * Constructor
     *
     * @param bundle the validator bundle to use
     */
    public FullValidatorFactory(final ValidatorBundle bundle)
    {
        super(bundle);
        syntaxFactory = new SyntaxFactory(bundle);
    }

    @Override
    public ValidationReport validateSchema(final ValidationContext context)
        throws JsonValidationFailureException
    {
        final JsonNode schema = context.getSchema();

        if (validated.contains(schema))
            return new FullValidationReport("");

        final Validator validator = syntaxFactory.getValidator(context);
        final ValidationReport report = validator.validate(context, schema);

        if (report.isSuccess())
            validated.add(schema);

        return report;
    }
}
