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
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.main.SchemaNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Second validator in the validation chain
 *
 * <p>This validator checks the schema syntax (as its name implies).
 * Validation stops if the schema is deemed invalid.</p>
 *
 * <p>Its {@link #next()} method always returns an
 * {@link InstanceJsonValidator}.</p>
 */
public final class SyntaxJsonValidator
    extends JsonValidator
{
    SyntaxJsonValidator(final JsonSchemaFactory factory,
        final SchemaNode schemaNode)
    {
        super(factory, schemaNode);
    }

    @Override
    public boolean validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final List<String> messages = new ArrayList<String>();
        final JsonNode node = schemaNode.getNode();

        /*
         * Note that the JsonNode we have grabbed may not be a JSON Object!
         * This is the role of the .validateSyntax() method of our factory to
         * detect that.
         */
        factory.validateSyntax(messages, node);
        report.addMessages(messages);

        /*
         * We continue if and only if syntax validation succeeded, that is, the
         * list of syntax validation messages is empty.
         */
        return messages.isEmpty();
    }

    @Override
    public JsonValidator next()
    {
        return new InstanceJsonValidator(factory, schemaNode);
    }
}
