/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.ValidationState;
import org.codehaus.jackson.JsonNode;

public final class AdditionalItemsKeywordValidator
    extends AbstractKeywordValidator
{
    private final boolean shortcut;

    private int itemsCount = 0;

    public AdditionalItemsKeywordValidator(final JsonNode schema)
    {
        super(schema);

        shortcut = schema.get("additionalItems").asBoolean(true);

        final JsonNode node = schema.path("items");

        if (node.isArray())
            itemsCount = node.size();
    }

    @Override
    public void validate(final ValidationState state, final JsonNode node)
    {
        if (shortcut)
            return;

        if (node.size() > itemsCount)
            state.addMessage("element count of array (" + node.size()
                + ") is greater than what is allowed");
    }
}
