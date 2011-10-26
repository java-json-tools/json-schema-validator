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

import eel.kitchen.jsonschema.v2.schema.Schema;
import eel.kitchen.jsonschema.v2.schema.SchemaFactory;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

abstract class TypesetKeywordValidator
    extends AbstractKeywordValidator
{
    private static final String ANY = "any";

    private final JsonNode typeNode;
    protected final EnumSet<NodeType> typeSet = EnumSet.noneOf(NodeType.class);
    protected final Set<JsonNode> nextSchemas = new LinkedHashSet<JsonNode>();

    //TODO: this really should not be here
    protected final SchemaFactory factory = new SchemaFactory();
    protected Schema nextSchema = null;

    protected TypesetKeywordValidator(final String field, final JsonNode schema)
    {
        super(schema);
        typeNode = schema.get(field);
        setUp();
    }

    @Override
    public final Schema getNextSchema()
    {
        return nextSchema;
    }

    protected abstract void buildNext();

    private void setUp()
    {
        String s;

        if (typeNode.isTextual()) {
            s = typeNode.getTextValue();
            if (ANY.equals(s))
                typeSet.addAll(EnumSet.allOf(NodeType.class));
            else
                typeSet.add(NodeType.valueOf(s.toUpperCase()));
            return;
        }

        for (final JsonNode element: typeNode) {
            if (!typeNode.isTextual())
                nextSchemas.add(element);
            else {
                s = typeNode.getTextValue();
                if (ANY.equals(s))
                    typeSet.addAll(EnumSet.allOf(NodeType.class));
                else
                typeSet.add(NodeType.valueOf(s.toUpperCase()));
            }
        }

        if (typeSet.contains(NodeType.NUMBER))
            typeSet.add(NodeType.INTEGER);

        if (!nextSchemas.isEmpty())
            buildNext();
    }
}
