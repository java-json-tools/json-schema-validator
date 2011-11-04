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

package eel.kitchen.jsonschema.keyword;

import eel.kitchen.jsonschema.ValidationReport;
import eel.kitchen.jsonschema.base.CombinedValidator;
import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.CollectionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.util.HashMap;
import java.util.Map;

public final class ExtendsValidator
    extends CombinedValidator
{
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    public ExtendsValidator(final ValidationContext context,
        final JsonNode instance)
    {
        super(context, instance);
        buildQueue();
    }

    private void buildQueue()
    {
        final KeywordValidatorFactory factory = context.getKeywordFactory();
        final ObjectNode baseNode = nodeFactory.objectNode();

        baseNode.putAll((ObjectNode) schema);

        final JsonNode extendsNode = baseNode.remove("extends");

        ValidationContext other = context.createContext(baseNode);

        queue.add(factory.getValidator(other, instance));

        JsonNode mergedNode;

        if (extendsNode.isObject()) {
            mergedNode = merge(baseNode, extendsNode);
            other = context.createContext(mergedNode);
            queue.add(factory.getValidator(other, instance));
            return;
        }

        for (final JsonNode node: extendsNode) {
            mergedNode = merge(baseNode, node);
            other = context.createContext(mergedNode);
            queue.add(factory.getValidator(other, instance));
        }
    }

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
