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

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.base.CombinedValidator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.jsonschema.factories.KeywordFactory;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Queue;

public abstract class TypeKeywordValidator
    extends CombinedValidator
{
    private static final String ANY = "any";

    private final JsonNode typeNode;

    protected final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);
    protected final Queue<JsonNode> schemas = new ArrayDeque<JsonNode>();


    protected TypeKeywordValidator(final ValidationContext context,
        final JsonNode instance, final String field)
    {
        super(context, instance);
        typeNode = context.getSchemaNode().get(field);
        setUp();
    }

    private void setUp()
    {
        if (typeNode.isTextual()) {
            addType(typeNode.getTextValue());
            return;
        }

        for (final JsonNode element: typeNode) {
            if (!element.isTextual()) {
                schemas.add(element);
                continue;
            }
            addType(element.getTextValue());
        }
    }

    private void addType(final String s)
    {
        if (ANY.equals(s)) {
            typeSet.addAll(EnumSet.allOf(NodeType.class));
            return;
        }

        typeSet.add(NodeType.valueOf(s.toUpperCase()));

        if (typeSet.contains(NodeType.NUMBER))
            typeSet.add(NodeType.INTEGER);
    }

    protected final void buildQueue()
    {
        final KeywordFactory factory = context.getKeywordFactory();

        ValidationContext other;

        while (!schemas.isEmpty()) {
            other = context.createContext(schemas.remove());
            queue.add(factory.getValidator(other, instance));
        }
    }
}
