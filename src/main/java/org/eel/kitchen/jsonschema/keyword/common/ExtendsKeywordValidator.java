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

package org.eel.kitchen.jsonschema.keyword.common;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;

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
     * A {@link JsonNodeFactory}, needed for {@link #merge(ObjectNode,
     * ObjectNode)}
     */
    private static final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

    private static final ExtendsKeywordValidator instance
        = new ExtendsKeywordValidator();

    private ExtendsKeywordValidator()
    {
        super("extends");
    }

    public static ExtendsKeywordValidator getInstance()
    {
        return instance;
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final ValidationReport report = context.createReport();
        final JsonNode schemaNode = context.getSchema();

        final ObjectNode baseNode = nodeFactory.objectNode();
        baseNode.putAll((ObjectNode) schemaNode);

        final JsonNode extendsNode = baseNode.remove("extends");

        ValidationContext current = context.withSchema(baseNode);

        Validator v;

        v = current.getValidator(instance);
        report.mergeWith(v.validate(current, instance));

        ObjectNode mergedNode;

        if (extendsNode.isObject()) {
            mergedNode = merge(baseNode, (ObjectNode) extendsNode);
            current = context.withSchema(mergedNode);
            v = current.getValidator(instance);
            report.mergeWith(v.validate(current, instance));
        } else for (final JsonNode node: extendsNode) {
            mergedNode = merge(baseNode, (ObjectNode) node);
            current = context.withSchema(mergedNode);
            v = current.getValidator(instance);
            report.mergeWith(v.validate(current, instance));
        }

        return report;
    }

    /**
     * Crude schema merge implementation
     *
     * <p>Given a base node and another node, first builds a copy of the base
     * node and forcefeeds all fields of the other node in this copy
     * (overwriting any node defined in the other node which existed in the base
     * node).</p>
     *
     * @param base the base node
     * @param other the other node
     * @return the merge node
     */
    private static ObjectNode merge(final ObjectNode base,
        final ObjectNode other)
    {
        final ObjectNode ret = nodeFactory.objectNode();

        ret.putAll(base);
        ret.putAll(other);

        return ret;
    }
}
