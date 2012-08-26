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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.RefResolverJsonValidator;

/**
 * The main validation class
 *
 * <p>This class is thread-safe: you can validate as many inputs as you
 * want with one instance.</p>
 *
 * <p>In order to build an instance, you need to go through a
 * {@link JsonSchemaFactory}.</p>
 */
public final class JsonSchema
{
    private final JsonSchemaFactory factory;
    private final SchemaContainer container;
    private final JsonNode schema;

    JsonSchema(final JsonSchemaFactory factory, final SchemaContainer container,
        final JsonNode schema)
    {
        this.factory = factory;
        this.container = container;
        this.schema = schema;
    }

    public ValidationReport validate(final JsonNode instance)
    {
        final ValidationContext context = new ValidationContext(factory);
        context.setContainer(container);

        final ValidationReport report = new ValidationReport();

        JsonValidator validator
            = new RefResolverJsonValidator(factory, schema);

        while (validator.validate(context, report, instance))
            validator = validator.next();

        return report;
    }
}
