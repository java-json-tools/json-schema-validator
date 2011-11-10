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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Keyword validator for the {@code extends} keyword (draft section
 * 5.26).</p>
 *
 * <p>While the draft makes it optional, this validator also supports
 * "multiple extends", that is, extension of several schemas at once.</p>
 */
public final class ExtendsKeywordValidator
    extends KeywordValidator
{
    /**
     * A {@link JsonNodeFactory}, needed for {@link #merge(JsonNode, JsonNode)}
     */
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    public ExtendsKeywordValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
        buildQueue();
    }

    /**
     * <p>Method called from constructor, which builds the necessary schemas in
     * {@link #queue}:</p>
     * <ul>
     *     <li>it first adds a validator for current schema node minus its
     *     {@code extends} keyword;</li>
     *     <li>it then merges this altered schema node with all extended
     *     schemas and adds the corresponding validator to the queue</li>
     * </ul>
     * @see #merge(JsonNode, JsonNode)
     */
    private void buildQueue()
    {
        final ObjectNode baseNode = nodeFactory.objectNode();

        baseNode.putAll((ObjectNode) context.getSchemaNode());

        final JsonNode extendsNode = baseNode.remove("extends");

        ValidationContext other = context.createContext(baseNode);

        queue.add(other.getValidator(instance));

        JsonNode mergedNode;

        if (extendsNode.isObject()) {
            mergedNode = merge(baseNode, extendsNode);
            other = context.createContext(mergedNode);
            queue.add(other.getValidator(instance));
            return;
        }

        for (final JsonNode node: extendsNode) {
            mergedNode = merge(baseNode, node);
            other = context.createContext(mergedNode);
            queue.add(other.getValidator(instance));
        }
    }

    /**
     * Crude schema merge implementation: given a base node and another node,
     * first builds a copy of the base node and forcefeeds all fields of the
     * other node in this copy (overwriting any existing fields in the base
     * node)
     *
     * @param baseNode the base node
     * @param otherNode the other node
     * @return the copy
     */
    private static JsonNode merge(final JsonNode baseNode,
        final JsonNode otherNode)
    {
        final Map<String, JsonNode>
            base = CollectionUtils.toMap(baseNode.getFields()),
            other = CollectionUtils.toMap(otherNode.getFields());

        final Map<String, JsonNode> ret = new HashMap<String, JsonNode>(base);
        ret.putAll(other);

        return nodeFactory.objectNode().putAll(ret);
    }

    /**
     * Validates the instance by first checking the base node,
     * then any extended node.
     *
     * @return the validation report
     */
    @Override
    public ValidationReport validate()
    {
        while (hasMoreElements()) {
            report.mergeWith(nextElement().validate());
            if (!report.isSuccess())
                break;
        }

        queue.clear();
        return report;
    }
}
