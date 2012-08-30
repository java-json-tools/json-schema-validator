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
import org.eel.kitchen.jsonschema.util.RhinoHelper;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Syntax validator for the {@code patternProperties} keyword
 */
public final class PatternPropertiesSyntaxChecker
    extends SimpleSyntaxChecker
{
    private static final SyntaxChecker instance
        = new PatternPropertiesSyntaxChecker();

    private PatternPropertiesSyntaxChecker()
    {
        super("patternProperties", NodeType.OBJECT);
    }

    public static SyntaxChecker getInstance()
    {
        return instance;
    }

    @Override
    void checkValue(final ValidationMessage.Builder msg,
        final List<ValidationMessage> messages, final JsonNode schema)
    {
        final SortedMap<String, JsonNode> properties
            = JacksonUtils.nodeToTreeMap(schema.get(keyword));

        String key;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: properties.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            msg.clearInfo().addInfo("key", key);
            if (!RhinoHelper.regexIsValid(entry.getKey())) {
                msg.setMessage("key is not a valid ECMA 262 regex");
                messages.add(msg.build());
                // No need to continue: even if we were to continue and check
                // the value, the latter would never be picked up anyway.
                continue;
            }
            if (value.isObject())
                continue;
            msg.setMessage("illegal key value")
                .addInfo("expected", NodeType.OBJECT)
                .addInfo("found", NodeType.getNodeType(value));
            messages.add(msg.build());
        }
    }
}
