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
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;

/**
 * Keyword validator for {@code additionalItems} (draft section 5.6)
 */
public final class AdditionalItemsKeywordValidator
    extends KeywordValidator
{

    public AdditionalItemsKeywordValidator()
    {
        super("additionalItems");
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchemaNode();

        final boolean shortcut = schema.get("additionalItems").asBoolean(true);

        if (shortcut)
            return report;

        final JsonNode itemsNode = schema.path("items");

        /*
         * Meh. If additionalItems is false and items is not an array,
         * it is clearly a logical error... But this will be left to logical
         * validators, when they are implemented.
         */
        final int itemsCount = itemsNode.isArray() ? itemsNode.size() : 0;

        if (instance.size() > itemsCount)
            report.addMessage("array only allows " + itemsCount + " item(s)");

        return report;
    }
}
