/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public abstract class TypeNodeSyntaxValidator
    extends SyntaxValidator
{
    private static final String ANY = "any";

    protected TypeNodeSyntaxValidator(final ValidationContext context,
        final String field)
    {
        super(context, field, NodeType.STRING, NodeType.ARRAY);
    }

    @Override
    protected final void checkFurther()
    {
        if (!node.isArray()) {
            validateOne(node);
            return;
        }

        int i = 0;
        for (final JsonNode element : node)
            validateOne(String.format("array element %d: ", i++), element);
    }

    private void validateOne(final String prefix, final JsonNode element)
    {
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
                    report.addMessage(String.format("%sunknown simple type %s",
                        prefix, s));
                }
                return;
            default:
                report.addMessage(String.format("%selement has wrong "
                    + "type %s (expected a simple type or a schema)",
                    prefix, type));
        }
    }

    private void validateOne(final JsonNode element)
    {
        validateOne("", element);
    }
}
