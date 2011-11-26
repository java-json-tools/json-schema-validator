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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword.common;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

/**
 * Keyword validator for {@code additionalItems} (draft section 5.6)
 */
public final class AdditionalItemsKeywordValidator
    extends KeywordValidator
{
    private static final AdditionalItemsKeywordValidator instance
        = new AdditionalItemsKeywordValidator();

    private AdditionalItemsKeywordValidator()
    {
        super("additionalItems");
    }

    public static AdditionalItemsKeywordValidator getInstance()
    {
        return instance;
    }

    /**
     * Validate {@code additionalItems}
     *
     * <p>The rules are simple: if {@code additionalItems} is anything else
     * than {@code false}, then the validation succeeds. Otherwise,
     * it must be checked that the {@code items} keyword, if it is an array,
     * has a number of elements less than or equal to the number of elements
     * in the instance.
     * </p>
     *
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @return the report
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchema();

        final boolean shortcut = schema.get(keyword).asBoolean(true);

        if (shortcut)
            return report;

        final JsonNode itemsNode = schema.path("items");

        /*
         * Meh. If additionalItems is false and items is not an array,
         * it is clearly a logical error... A user error, in other words.
         */
        final int itemsCount = itemsNode.isArray() ? itemsNode.size() : 0;

        if (instance.size() > itemsCount)
            report.fail("array only allows " + itemsCount + " item(s)");

        return report;
    }
}
