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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Syntax checker for the {@code dependencies} keyword
 */
public final class DependenciesSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final EnumSet<NodeType> VALID_DEPENDENCY_TYPES
        = EnumSet.of(NodeType.OBJECT, NodeType.ARRAY, NodeType.STRING);

    private static final SyntaxChecker instance
        = new DependenciesSyntaxChecker();

    private DependenciesSyntaxChecker()
    {
        super("dependencies", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final ValidationMessage.Builder msg,
        final List<ValidationMessage> messages, final JsonNode schema)
    {
        /*
         * At that point, we know this is an array. Build a map out of it and
         * call an internal validation method on each map entry -- see below.
         *
         * For convenience reasons, we also use a SortedMap so that messages
         * appear in property natural order: while there is no defined order in
         * a JSON Object, chances are very high that the schema will be written
         * with properties in order, so we might as well not confuse the user.
         */
        final SortedMap<String, JsonNode> map
            = JacksonUtils.nodeToTreeMap(schema.get(keyword));

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            analyzeDependency(entry, msg, messages);
    }

    /**
     * Analyze one entry in a {@code dependency} object entry
     *
     * @param entry the JSON object entry (as a map entry)
     * @param messages the validation message list
     */
    private static void analyzeDependency(
        final Map.Entry<String, JsonNode> entry,
        final ValidationMessage.Builder msg,
        final List<ValidationMessage> messages)
    {
        /**
         * The key is the propery name in the map entry, the value is this
         * property's value.
         */
        final String key = entry.getKey();
        final JsonNode value = entry.getValue();

        /*
         * A text value or object value alone means a single property dependency
         * or a schema dependency, respectively. These are all valid values
         * at this level, let them through.
         */
        if (value.isTextual() || value.isObject())
            return;

        /*
         * Add information to the message about the property key name.
         */
        msg.clearInfo().addInfo("property", key);

        /*
         * From now on, the only valid value type is an array. If it is not,
         * complain.
         */
        if (!value.isArray()) {
            msg.addInfo("found", NodeType.getNodeType(value))
                .addInfo("expected", VALID_DEPENDENCY_TYPES)
                .setMessage("dependency value has incorrect type");
            messages.add(msg.build());
            return;
        }

        /*
         * If it _is_ an array (the only possible scenario at that point), all
         * members in this array MUST be potential property names, ie JSON
         * strings. If one isn't, record all of:
         *
         *  - the key name in dependencites;
         *  - the value type encountered;
         *  - the error message;
         *  - the index in the array.
         */
        int idx = 0;
        NodeType type;

        for (final JsonNode element: value) {
            type = NodeType.getNodeType(element);
            if (NodeType.STRING != type) {
                msg.addInfo("index", idx).addInfo("found", type)
                    .addInfo("expected", NodeType.STRING)
                    .setMessage("array dependency value has incorrect type");
                messages.add(msg.build());
            }
            idx++;
        }
    }
}
