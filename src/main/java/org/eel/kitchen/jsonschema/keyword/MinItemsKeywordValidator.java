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
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code minItems} keyword
 */
public final class MinItemsKeywordValidator
    extends PositiveIntegerKeywordValidator
{
    public MinItemsKeywordValidator(final JsonNode schema)
    {
        super("minItems", schema, NodeType.ARRAY);
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (instance.size() >= intValue)
            return;

        final ValidationMessage.Builder msg = newMsg()
            .addInfo(keyword, intValue).addInfo("found", instance.size())
            .setMessage("not enough elements in array");
        report.addMessage(msg.build());
    }
}
