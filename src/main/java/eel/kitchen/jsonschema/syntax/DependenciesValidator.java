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

package eel.kitchen.jsonschema.syntax;

import eel.kitchen.jsonschema.context.ValidationContext;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import org.codehaus.jackson.JsonNode;

import java.util.Map;
import java.util.SortedMap;

public final class DependenciesValidator
    extends SyntaxValidator
{
    public DependenciesValidator(final ValidationContext context)
    {
        super(context, "dependencies", NodeType.OBJECT);
    }

    @Override
    protected void checkFurther()
    {

        final SortedMap<String, JsonNode> fields
            = CollectionUtils.toSortedMap(node.getFields());

        String field;
        JsonNode element;
        NodeType type;

        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            field = entry.getKey();
            element = entry.getValue();
            type = NodeType.getNodeType(element);
            switch (type) {
                case STRING: case OBJECT:
                    break;
                case ARRAY:
                    checkDependencyArray(field, element);
                    break;
                default:
                    report.addMessage(String.format("field \"%s\": illegal "
                        + "value of type %s", field, type));
            }
        }
    }

    private void checkDependencyArray(final String field, final JsonNode node)
    {
        NodeType type;
        String message;
        int i = -1;

        for (final JsonNode element: node) {
            i++;
            type = NodeType.getNodeType(element);
            if (type == NodeType.STRING)
                continue;
            message = String.format("field \"%s\": array element %d has wrong "
                + "type %s, expected a property name", field, i, type);
            report.addMessage(message);
        }
    }
}
