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

package org.eel.kitchen.jsonschema.syntax;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Specialized syntax validator used to validate URIs
 *
 * @see URI
 */
public abstract class URISyntaxValidator
    extends SyntaxValidator
{
    protected URISyntaxValidator(final String keyword)
    {
        super(keyword, NodeType.STRING);
    }

    @Override
    protected final void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        final JsonNode node = schema.get(keyword);

        try {
            new URI(node.getTextValue());
        } catch (URISyntaxException ignored) {
            report.fail("not a valid URI");
        }
    }
}
