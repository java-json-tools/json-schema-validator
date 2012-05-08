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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

public class AdditionalItemsKeywordValidator
    extends KeywordValidator
{
    private boolean additionalOK;
    private int itemsCount = 0;

    public AdditionalItemsKeywordValidator(final JsonNode schema)
    {
        super(NodeType.ARRAY);
        additionalOK = schema.get("additionalItems").asBoolean(true);

        if (additionalOK)
            return;

        final JsonNode items = schema.path("items");

        if (items.isArray())
            itemsCount = items.size();
        else
            additionalOK = true;
    }
    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        if (additionalOK)
            return;

        if (instance.size() > itemsCount)
            report.addMessage("additional items not permitted");
    }
}
