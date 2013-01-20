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

package com.github.fge.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.PositiveIntegerKeywordValidator;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code maxLength} keyword
 */
public final class MaxLengthKeywordValidator
    extends PositiveIntegerKeywordValidator
{
    public MaxLengthKeywordValidator(final JsonNode schema)
    {
        super("maxLength", schema, NodeType.STRING);
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final int len = instance.textValue().length();
        if (len <= intValue)
            return;

        final Message.Builder msg = newMsg().addInfo(keyword, intValue)
            .addInfo("found", len).setMessage("string is too long");
        report.addMessage(msg.build());
    }
}
