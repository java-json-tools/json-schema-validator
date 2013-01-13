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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.List;

/**
 * The simplest syntax checker
 *
 * <p>Basic syntax checker which only checks the type of its keyword, without
 * performing any further analysis.</p>
 */
public final class TypeOnlySyntaxChecker
    extends AbstractSyntaxChecker
{

    public TypeOnlySyntaxChecker(final String keyword, final NodeType type,
        final NodeType... types)
    {
        super(keyword, type, types);
    }

    @Override
    public void checkValue(SyntaxValidator validator, final Message.Builder msg,
        final List<Message> messages, final JsonNode schema)
    {
    }
}
