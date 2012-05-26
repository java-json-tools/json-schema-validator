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
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.util.Collections;
import java.util.EnumSet;

public abstract class SyntaxChecker
{
    protected final String keyword;
    private final EnumSet<NodeType> validTypes;

    protected SyntaxChecker(final String keyword)
    {
        this.keyword = keyword;
        validTypes = getValidTypes();
    }

    private EnumSet<NodeType> getValidTypes()
    {
        final ValidTypes ann = getClass().getAnnotation(ValidTypes.class);

        if (ann == null)
            return null;

        final EnumSet<NodeType> ret = EnumSet.noneOf(NodeType.class);

        Collections.addAll(ret, ann.value());
        return ret;
    }

    public final void checkSyntax(final ValidationReport report,
        final JsonNode schema)
    {
        if (validTypes != null) {
            final NodeType nodeType = NodeType.getNodeType(schema.get(keyword));
            if (!validTypes.contains(nodeType)) {
                report.addMessage("keyword is of wrong type");
                return;
            }

        }
        checkValue(report, schema);
    }

    abstract void checkValue(final ValidationReport report,
        final JsonNode schema);
}
