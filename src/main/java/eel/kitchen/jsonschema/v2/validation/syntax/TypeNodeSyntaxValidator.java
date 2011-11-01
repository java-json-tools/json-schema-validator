/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.v2.validation.syntax;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public abstract class TypeNodeSyntaxValidator
    extends SyntaxValidator
{
    private static final String ANY = "any";

    protected TypeNodeSyntaxValidator(final JsonNode schemaNode,
        final String field)
    {
        super(schemaNode, field, NodeType.STRING, NodeType.ARRAY);
    }

    @Override
    protected final void checkFurther()
    {
        //TODO: implement
        if (!node.isArray()) {
            validateOne(node);
            return;
        }

        for (final JsonNode element : node) {
            validateOne(element);
            if (!report.isSuccess())
                return;
        }
    }

    private void validateOne(final JsonNode element)
    {
        //TODO: implement
        final NodeType type = NodeType.getNodeType(element);

        switch (type) {
            case OBJECT:
                return;
            case STRING:
                final String s = element.getTextValue();
                try {
                    if (!ANY.equals(s))
                        NodeType.valueOf(s.toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    report.addMessage(keyword + ": unknown type " + s);
                }
                return;
            default:
                report.addMessage(
                    "invalid element of type " + type + " in " + keyword
                        + " array");
        }
    }
}
