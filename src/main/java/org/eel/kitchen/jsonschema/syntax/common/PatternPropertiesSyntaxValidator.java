/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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
import org.eel.kitchen.util.RhinoHelper;

import java.util.Map;
import java.util.SortedMap;

public final class PatternPropertiesSyntaxValidator
    extends SyntaxValidator
{
    private static final PatternPropertiesSyntaxValidator instance
        = new PatternPropertiesSyntaxValidator();

    private PatternPropertiesSyntaxValidator()
    {
        super("patternProperties", NodeType.OBJECT);
    }

    public static PatternPropertiesSyntaxValidator getInstance()
    {
        return instance;
    }

    /**
     * Check that all keys are valid regexes, and that all values are objects
     *
     * @see RhinoHelper#regexIsValid(String)
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
            if (!RhinoHelper.regexIsValid(field))
                report.message(String.format("field \"%s\": regex is invalid",
                    field));
            element = entry.getValue();
            type = NodeType.getNodeType(element);
            if (type == NodeType.OBJECT)
                continue;
            report.message(String.format("field \"%s\": value has wrong "
                + "type %s (expected a schema)", field, type));
        }
    }
}
