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
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Keyword validator for the {@code extends} keyword (draft section 5.26)
 *
 * <p>While the draft makes it optional, this validator also supports
 * "multiple extends", that is, extension of several schemas at once.</p>
 */
public final class ExtendsKeywordValidator
    extends KeywordValidator
{
    /**
     * A {@link JsonNodeFactory}, needed for {@link #merge(Map, JsonNode)}
     */
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    public ExtendsKeywordValidator()
    {
        super("extends");
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        final JsonNode schemaNode = context.getSchemaNode();

        final ObjectNode baseNode = nodeFactory.objectNode();
        baseNode.putAll((ObjectNode) schemaNode);

        final JsonNode extendsNode = baseNode.remove("extends");

        final Map<String, JsonNode> map
            = CollectionUtils.toMap(baseNode.getFields());

        ValidationContext current = context.createContext(baseNode);

        Validator v;

        v = current.getValidator(instance);
        report.mergeWith(v.validate(current, instance));

        JsonNode mergedNode;

        if (extendsNode.isObject()) {
            mergedNode = merge(map, extendsNode);
            current = context.createContext(mergedNode);
            v = current.getValidator(instance);
            report.mergeWith(v.validate(current, instance));
        } else for (final JsonNode node: extendsNode) {
            mergedNode = merge(map, node);
            current = context.createContext(mergedNode);
            v = current.getValidator(instance);
            report.mergeWith(v.validate(current, instance));
        }

        return report;
    }

    /**
     * Crude schema merge implementation
     *
     * <p>Given a base node and another node,
     * first builds a copy of the base node and forcefeeds all fields of the
     * other node in this copy (overwriting any node defined in the other
     * node which existed in the base node).</p>
     *
     * @param map the field/node map of the schema node, minus the
     * {@code extends} field
     * @param otherNode the other node
     * @return the copy
     */
    private static JsonNode merge(final Map<String, JsonNode> map,
        final JsonNode otherNode)
    {
        final Map<String, JsonNode>
            ret = new HashMap<String, JsonNode>(map),
            other = CollectionUtils.toMap(otherNode.getFields());

        ret.putAll(other);

        return nodeFactory.objectNode().putAll(ret);
    }
}
