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

package eel.kitchen.jsonschema.v2.keyword;

import eel.kitchen.jsonschema.v2.schema.MatchAllSchema;
import eel.kitchen.jsonschema.v2.schema.MatchAnySchema;
import eel.kitchen.jsonschema.v2.schema.NegativeMatchSchema;
import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.SchemaFactory;
import eel.kitchen.jsonschema.v2.schema.SingleSchema;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class TypeDisallowKeywordValidator
    extends AbstractKeywordValidator
{
    private static final String ANY = "any";

    private final EnumSet<NodeType> validTypes = EnumSet.noneOf(NodeType.class);

    private final JsonNode typeNode, disallowNode;

    private boolean simpleTypesOnly;
    private Schema next;

    public TypeDisallowKeywordValidator(final JsonNode schema)
    {
        super(schema);
        typeNode = schema.path("type");
        disallowNode = schema.path("disallow");
        setup();
    }

    @Override
    public ValidationStatus validate(final JsonNode instance)
    {
        if (!simpleTypesOnly)
            return ValidationStatus.DUNNO;

        final NodeType type = NodeType.getNodeType(instance);
        if (validTypes.contains(type))
            return ValidationStatus.SUCCESS;

        messages.add("instance is of type " + type + ", expected one of "
            + validTypes);
        return ValidationStatus.FAILURE;
    }

    @Override
    public Schema getNextSchema()
    {
        return next;
    }

    private void setup()
    {
        final Set<String>
            allowed = new HashSet<String>(),
            denied = new HashSet<String>();
        final Set<JsonNode>
            typeSchemas = new HashSet<JsonNode>(),
            disallowSchemas = new HashSet<JsonNode>();

        if (typeNode.isMissingNode())
            allowed.add(ANY);
        else if (typeNode.isTextual())
            allowed.add(typeNode.getTextValue());
        else
            for (final JsonNode element: typeNode)
                if (element.isTextual())
                    allowed.add(element.getTextValue());
                else
                    typeSchemas.add(element);

        if (disallowNode.isMissingNode())
            denied.add(ANY);
        else if (disallowNode.isTextual())
            denied.add(disallowNode.getTextValue());
        else
            for (final JsonNode element: disallowNode)
                if (element.isTextual())
                    denied.add(element.getTextValue());
                else
                    disallowSchemas.add(element);


        computeValidTypes(allowed, denied);
        simpleTypesOnly = typeSchemas.isEmpty() &&  disallowSchemas.isEmpty();
        next = computeNextSchema(typeSchemas, disallowSchemas);
    }

    private static Schema computeNextSchema(final Set<JsonNode> typeSchemas,
        final Set<JsonNode> disallowSchemas)
    {
        final Schema allow = computeSchema(typeSchemas);
        final Schema deny = computeSchema(disallowSchemas);

        if (deny == null)
            return allow;
        if (allow == null)
            return deny;

        final Set<Schema> ret = new LinkedHashSet<Schema>();

        ret.add(allow);
        ret.add(new NegativeMatchSchema(deny));

        return new MatchAllSchema(ret);
    }

    private static Schema computeSchema(final Set<JsonNode> schemas)
    {
        if (schemas.isEmpty())
            return null;

        final SchemaFactory factory = new SchemaFactory();

        if (schemas.size() == 1)
            return new SingleSchema(factory, schemas.iterator().next());

        final Set<Schema> set = new LinkedHashSet<Schema>();

        for (final JsonNode node: schemas)
            set.add(new SingleSchema(factory, node));

        return new MatchAnySchema(set);
    }

    private void computeValidTypes(final Set<String> allowed,
        final Set<String> denied)
    {
        NodeType type;

        for (final String s: allowed) {
            if (ANY.equals(s)) {
                validTypes.addAll(EnumSet.allOf(NodeType.class));
                break;
            }
            type = NodeType.valueOf(s.toUpperCase());
            validTypes.add(type);
            if (NodeType.NUMBER == type)
                validTypes.add(NodeType.INTEGER);
        }

        for (final String s: denied) {
            if (ANY.equals(s)) {
                validTypes.clear();
                return;
            }
            type = NodeType.valueOf(s.toUpperCase());
            validTypes.remove(type);
            if (NodeType.NUMBER == type)
                validTypes.remove(NodeType.INTEGER);
        }
    }
}
