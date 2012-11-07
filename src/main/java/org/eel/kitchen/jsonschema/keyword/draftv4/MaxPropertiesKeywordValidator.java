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

package org.eel.kitchen.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.PositiveIntegerKeywordValidator;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code maxProperties} keyword
 *
 * <p>This keyword is not defined by draft v3, and a candidate for the next
 * draft. It places au upper constraint on the number of members of an object
 * instance in the same manner than {@code maxItems} does for array instances.
 * </p>
 */
public final class MaxPropertiesKeywordValidator
    extends PositiveIntegerKeywordValidator
{
    public MaxPropertiesKeywordValidator(final JsonNode schema)
    {
        super("maxProperties", schema, NodeType.OBJECT);
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (instance.size() <= intValue)
            return;

        final Message.Builder msg = newMsg().addInfo(keyword, intValue)
            .addInfo("found", instance.size())
            .setMessage("too many members in object");
        report.addMessage(msg.build());
    }
}
