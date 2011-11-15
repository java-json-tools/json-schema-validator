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

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * Keyword validator for the {@code enum} keyword (draft section 5.19).
 * Jackson is of great help here, since {@link JsonNode#equals(Object)} works
 * perfectly <i>and</i> recursively for container nodes.
 */
public final class EnumKeywordValidator
    extends KeywordValidator
{
    public EnumKeywordValidator()
    {
        super("enum");
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        final JsonNode enumNode = context.getSchemaNode().get(keyword);

        for (final JsonNode element: enumNode)
            if (element.equals(instance))
                return report;

        report.addMessage("instance does not match any member of the "
            + "enumeration");

        return report;
    }
}
