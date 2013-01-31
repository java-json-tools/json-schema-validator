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

package com.github.fge.jsonschema.old.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.old.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.old.syntax.SyntaxValidator;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.RhinoHelper;

import java.util.List;

/**
 * Syntax validator for the {@code pattern} keyword
 *
 * @see RhinoHelper
 */
public final class PatternSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final PatternSyntaxChecker INSTANCE
        = new PatternSyntaxChecker();

    public static PatternSyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private PatternSyntaxChecker()
    {
        super("pattern", NodeType.STRING);
    }

    @Override
    public void checkValue(final SyntaxValidator validator,
        final List<Message> messages, final JsonNode schema)
    {
        final String value = schema.get(keyword).textValue();
        if (RhinoHelper.regexIsValid(value))
            return;

        messages.add(newMsg().addInfo("found", value)
            .setMessage("pattern is not a valid ECMA 262 regex").build());
    }
}
