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
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.NodeType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

    protected AbstractTypeKeywordValidator(final String keyword)
    {
        super(keyword);
    }

    protected abstract ValidationReport doValidate(
        final ValidationContext context, final JsonNode instance,
        final EnumSet<NodeType> typeSet, final List<JsonNode> schemas);

    @Override
    public final ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final JsonNode schema = context.getSchemaNode();
        final JsonNode typeNode = schema.get(keyword);
        final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);
        final List<JsonNode> schemas = new ArrayList<JsonNode>();

        prepare(typeNode, typeSet, schemas);
        return doValidate(context, instance, typeSet, schemas);
    }

    protected static ValidationReport validateSchema(
        final ValidationContext context, final JsonNode schema,
        final JsonNode instance)
    {
        final ValidationContext ctx = context.createContext(schema);

        return ctx.getValidator(instance).validate(ctx, instance);
    }

    private void prepare(final JsonNode typeNode,
        final EnumSet<NodeType> typeSet, final List<JsonNode> schemas)
    {
        if (typeNode.isTextual()) {
            addType(typeNode.getTextValue(), typeSet);
            return;
        }

        for (final JsonNode element: typeNode) {
            if (!element.isTextual()) {
                schemas.add(element);
                continue;
            }
            addType(element.getTextValue(), typeSet);
        }
    }

    private static void addType(final String s, final EnumSet<NodeType>
        typeSet)
    {
        if (ANY.equals(s)) {
            typeSet.addAll(EnumSet.allOf(NodeType.class));
            return;
        }

        typeSet.add(NodeType.valueOf(s.toUpperCase()));

        if (typeSet.contains(NodeType.NUMBER))
            typeSet.add(NodeType.INTEGER);
    }
}
