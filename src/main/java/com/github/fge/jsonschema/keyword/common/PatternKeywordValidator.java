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
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.RhinoHelper;
import com.github.fge.jsonschema.validator.ValidationContext;

/**
 * Validator for the {@code pattern} keyword
 *
 * <p>Regexes must conform to ECMA 262, so, again, this makes {@link
 * java.util.regex} unusable.</p>
 *
 * @see RhinoHelper
 */
public final class PatternKeywordValidator
    extends KeywordValidator
{
    private final String regex;

    public PatternKeywordValidator(final JsonNode schema)
    {
        super("pattern", NodeType.STRING);
        regex = schema.get(keyword).textValue();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (RhinoHelper.regMatch(regex, instance.textValue()))
            return;

        final Message.Builder msg = newMsg().addInfo("regex", regex)
            .addInfo("string", instance)
            .setMessage("ECMA 262 regex does not match input string");
        report.addMessage(msg.build());
    }

    @Override
    public String toString()
    {
        return keyword + ": " + regex;
    }
}
