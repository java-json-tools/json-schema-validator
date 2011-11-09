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

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.NodeType;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Queue;

/**
 * A {@link KeywordValidator} specialized in validating the {@code type} and
 * {@code disallow} keywords.
 *
 * @see TypeKeywordValidator
 * @see DisallowKeywordValidator
 */
public abstract class AbstractTypeKeywordValidator
    extends KeywordValidator
{
    /**
     * String matching "any" type
     */
    private static final String ANY = "any";

    /**
     * The schema node
     */
    private final JsonNode typeNode;

    /**
     * The list of simple types declared by the keyword
     */
    protected final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);

    /**
     * The list of schemas found in the keyword
     */
    protected final Queue<JsonNode> schemas = new ArrayDeque<JsonNode>();


    /**
     * Constructor. Initializes {@link #typeNode}, and then calls {@link
     * #setUp()} to fill in {@link #typeSet} and {@link #schemas}.
     *
     * @param context the context to use
     * @param instance the instance to validate
     * @param field the name of the keyword ({@code type} or {@code disallow}
     */
    protected AbstractTypeKeywordValidator(final ValidationContext context,
        final JsonNode instance, final String field)
    {
        super(context, instance);
        typeNode = context.getSchemaNode().get(field);
        setUp();
    }

    /**
     * <p>Fills in the {@link #typeSet} and {@link #schemas} instance
     * variables:</p>
     * <ul>
     *     <li>if the type node is a simple text node, registers the matching
     *     type(s) in {@link #typeSet} (using {@link #addType(String)};</li>
     *     <li>if it is an array, register either the simple type or the
     *     schema.</li>
     * </ul>
     */
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

    /**
     * Add a simple type to the {@link #typeSet} enum set. If the argument is
     * {@code "any"}, registers all types. If the argument is {@code
     * "number"}, also registers {@code "integer"} since the latter is a
     * subset of the former.
     *
     * @param s the simple type as a string
     * @see NodeType
     */
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

    /**
     * Builds the schema queue in {@link #queue} if {@link #schemas} is not
     * empty.
     */
    protected final void buildQueue()
    {
        ValidationContext other;

        while (!schemas.isEmpty()) {
            other = context.createContext(schemas.remove());
            queue.add(other.getValidator(instance));
        }
    }
}
