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

package org.eel.kitchen.jsonschema.syntax.common;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.NodeType;

/**
 * Syntax validator for the {@code extends} keyword
 *
 * <p>Here again, simple type checking is not enough: if the value of {@code
 * extends} is an array, we must check that all elements of the array are
 * objects.</p>
 */
public final class ExtendsSyntaxValidator
    extends SyntaxValidator
{
    private static final ExtendsSyntaxValidator instance
        = new ExtendsSyntaxValidator();

    public static ExtendsSyntaxValidator getInstance()
    {
        return instance;
    }

    private ExtendsSyntaxValidator()
    {
        super("extends", NodeType.OBJECT, NodeType.ARRAY);
    }

    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
        throws JsonValidationFailureException
    {
        final JsonNode node = schema.get(keyword);

        if (node.isObject())
            return;

        int i = -1;
        NodeType type;
        for (final JsonNode element: node) {
            i++;
            type = NodeType.getNodeType(element);
            if (type == NodeType.OBJECT)
                continue;
            report.fail(String.format(
                "array element %d has wrong " + "type %s (expected a schema)",
                i, type));
        }
    }
}
