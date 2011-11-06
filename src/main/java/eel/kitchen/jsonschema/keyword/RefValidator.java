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

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.CombinedValidator;
import eel.kitchen.jsonschema.base.Validator;
import eel.kitchen.jsonschema.context.ValidationContext;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

public final class RefValidator
    extends CombinedValidator
{
    public RefValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
    }

    @Override
    public ValidationReport validate()
    {
        final JsonNode schemaNode = context.getSchemaNode();
        final String ref = schemaNode.get("$ref").getTextValue();

        final JsonNode next;

        try {
            next = context.resolve(ref);
        } catch (IOException e) {
            report.addMessage(String.format("cannot resolve ref %s: %s: %s",
                ref, e.getClass().getName(), e.getMessage()));
            return report;
        }

        if (next.equals(schemaNode)) {
            report.addMessage(String.format("ref %s points to myself!", ref));
            return report;
        }

        final Validator v = context.createContext(next).getValidator(instance);

        return v.validate();
    }
}
