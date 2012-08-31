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
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.Set;

/**
 * Validator for the {@code enum} keyword
 */
public final class EnumKeywordValidator
    extends KeywordValidator
{
    private final JsonNode enumNode;
    private final Set<JsonNode> enumValues;

    public EnumKeywordValidator(final JsonNode schema)
    {
        super("enum", NodeType.values());
        enumNode = schema.get(keyword);
        enumValues = ImmutableSet.copyOf(enumNode);
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (enumValues.contains(instance))
            return;

        final ValidationMessage.Builder msg = newMsg()
            .setMessage("value not found in enum").addInfo("enum", enumNode)
            .addInfo("value", instance);
        report.addMessage(msg.build());
    }

    @Override
    public String toString()
    {
        /*
         * Enum values may be arbitrarily complex: we therefore choose to only
         * print the number of possible values instead of each possible value.
         *
         * By virtue of syntax validation, we also know that enumValues will
         * never be empty.
         */
        return keyword + ": " + enumValues.size() + " possible value(s)";
    }
}
