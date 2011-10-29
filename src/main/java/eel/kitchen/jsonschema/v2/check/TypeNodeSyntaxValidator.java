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

package eel.kitchen.jsonschema.v2.check;

import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

public abstract class TypeNodeSyntaxValidator
    extends MultipleTypeSyntaxValidator
{
    private static final String ANY  = "any";

    private final String keyword;

    protected TypeNodeSyntaxValidator(final String keyword)
    {
        super(keyword, NodeType.STRING, NodeType.ARRAY);
        this.keyword = keyword;
    }

    @Override
    public final boolean validate(final JsonNode schema)
    {
        if (!super.validate(schema))
            return false;
        final JsonNode node = schema.get(keyword);

        if (!node.isArray())
            return validateOne(node);

        // TODO: implement
        messages.add("Sorry, I only support one simple type for now");
        return false;
//        boolean ret = true;
//
//        for (final JsonNode element: node)
//            ret = validateOne(element) && ret;
//
//        return ret;
    }

    private boolean validateOne(final JsonNode element)
    {
        final NodeType type = NodeType.getNodeType(element);

        switch (type) {
            case OBJECT:
                // TODO: implement
                //return true;
                messages.add("Sorry, I only support one simple type for now");
                return false;
            case STRING:
                final String s = element.getTextValue();
                if (ANY.equals(s))
                    return true;
                try {
                    NodeType.valueOf(s.toUpperCase());
                    return true;
                } catch (IllegalArgumentException e) {
                    messages.add("unknown type " + s);
                    return false;
                }
            default:
                messages.add("invalid element of type " + type + " in "
                    + keyword + " array");
                return false;
        }
    }
}
