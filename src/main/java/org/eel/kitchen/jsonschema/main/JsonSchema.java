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

public final class JsonSchema
{
    private final JsonSchemaFactory factory;
    private final SchemaNode schemaNode;
    private final ValidationContext context;

    public JsonSchema(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        this.factory = factory;
        this.schemaNode = schemaNode;
        context = new ValidationContext(factory);
        context.setContainer(schemaNode.getContainer());
    }

    public JsonSchema(final JsonSchemaFactory factory,
        final ValidationContext context, final SchemaNode schemaNode)
    {
        this.factory = factory;
        this.context = context;
        this.schemaNode = schemaNode;
    }

    public ValidationReport validate(final JsonNode instance)
    {
        final ValidationReport report = new ValidationReport();

        JsonValidator validator
            = new RefResolverJsonValidator(factory, schemaNode);

        while (validator.validate(context, report, instance))
            validator = validator.next();

        return report;
    }
}
