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

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class AbstractKeywordValidator
    implements KeywordValidator
{
    protected final JsonNode schema;
    protected final EnumSet<NodeType> nodeTypes = EnumSet.noneOf(NodeType.class);
    protected final List<String> messages = new LinkedList<String>();
    protected final Map<String, EnumSet<NodeType>> fields
        = new HashMap<String, EnumSet<NodeType>>();

    protected AbstractKeywordValidator(final JsonNode schema,
        final NodeType... types)
    {
        this.schema = schema;
        nodeTypes.addAll(Arrays.asList(types));
    }

    protected abstract void setup();

    protected abstract boolean doValidate(final JsonNode instance);

    @Override
    public final EnumSet<NodeType> getNodeTypes()
    {
        return EnumSet.copyOf(nodeTypes);
    }

    @Override
    public final List<String> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public final boolean validate(final JsonNode instance)
    {
        if (!checkFields())
            return false;

        setup();

        return doValidate(instance);
    }

    private boolean checkFields()
    {
        final Iterator<String> iterator = schema.getFieldNames();

        boolean ret = true;

        String field;
        NodeType nodeType;
        EnumSet<NodeType> typeSet;

        while (iterator.hasNext()) {
            field = iterator.next();
            if (!fields.containsKey(field))
                continue;
            nodeType = NodeType.getNodeType(schema.get(field));
            typeSet = fields.get(field);
            if (typeSet.contains(nodeType))
                continue;
            messages.add("invalid schema: " + field + " is of type " + nodeType
                + ", should be one of " + typeSet);
            ret = false;
        }

        return ret;
    }

    protected final void registerField(final String name,
        final NodeType... types)
    {
        fields.put(name, EnumSet.copyOf(Arrays.asList(types)));
    }
}
