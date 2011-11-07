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

import eel.kitchen.jsonschema.context.ValidationContext;
import org.codehaus.jackson.JsonNode;

/**
 * Keyword validator for {@code additionalItems} (draft section 5.6)
 */
public final class AdditionalItemsValidator
    extends SimpleKeywordValidator
{
    /**
     * Should we get out early? True if {@code additionalItems} is not a
     * boolean, or is a boolean set to true.
     */
    private final boolean shortcut;

    /**
     * Number of items in {@code items} (0 if it is not an array)
     */
    private final int itemsCount;

    public AdditionalItemsValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);

        shortcut = schema.get("additionalItems").asBoolean(true);

        final JsonNode itemsNode = schema.path("items");

        itemsCount = itemsNode.isArray() ? itemsNode.size() : 0;
    }

    /**
     * <p>Validate {@code additionalItems}:</p>
     * <ul>
     *     <li>if it is not a boolean, or it is set to {@code true},
     *     then the validation succeeds;</li>
     *     <li>otherwise, compare the number of schemas registered in the
     *     {@code items} keyword with the number of elements in the
     *     instance: if the latter is greater than the former,
     *     this is a validation failure.</li>
     * </ul>
     */
    @Override
    protected void validateInstance()
    {
        if (shortcut)
            return;

        if (instance.size() > itemsCount)
            report.addMessage("array only allows " + itemsCount + " item(s)");
    }
}
