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

package org.eel.kitchen.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.Map;
import java.util.SortedMap;

/**
 * <p>Syntax validator for the {@code dependencies} keyword</p>
 *
 * <p>This is one of the keywords where type checking alone is not enough,
 * we must also ensure that the different children are correct. They can
 * either be:</p>
 * <ul>
 *     <li>one simple dependency,</li>
 *     <li>an array of simple dependencies, or</li>
 *     <li>a schema (ie, an object).</li>
 * </ul>
 */
public final class DependenciesSyntaxValidator
    extends SyntaxValidator
{
    private static final DependenciesSyntaxValidator instance
        = new DependenciesSyntaxValidator();


    public static DependenciesSyntaxValidator getInstance()
    {
        return instance;
    }

    private DependenciesSyntaxValidator()
    {
        super("dependencies", NodeType.OBJECT);
    }

    /**
     * Walks the different elements of the dependencies object and checks
     * their correctness (see description). Calls {@link
     * #checkDependencyArray(ValidationReport, String, JsonNode)} for arrays.
     */
    @Override
    protected void checkFurther(final JsonNode schema,
        final ValidationReport report)
    {
        final JsonNode node = schema.get(keyword);

        final SortedMap<String, JsonNode> fields
            = CollectionUtils.toSortedMap(node.fields());

        String field;
        JsonNode element;
        NodeType type;

        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            field = entry.getKey();
            element = entry.getValue();
            type = NodeType.getNodeType(element);
            switch (type) {
                case ARRAY:
                    checkDependencyArray(report, field, element);
                    // Fall through
                case STRING: case OBJECT:
                    break;
                default:
                    report.message(String.format(
                        "field \"%s\": illegal " + "value of type %s", field,
                        type));
            }
        }
    }

    /**
     * Checks the syntax of a dependency array, ie that it only contains
     * simple dependencies
     *
     * @param report the report to use
     * @param field the field name
     * @param node the array node for this field
     */
    private static void checkDependencyArray(final ValidationReport report,
        final String field, final JsonNode node)
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
            report.message(message);
        }
    }
}
