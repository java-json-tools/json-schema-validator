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

package eel.kitchen.jsonschema.v2.syntax;

import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.NodeType;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.Map;

public final class PatternPropertiesSyntaxValidator
    extends SingleTypeSyntaxValidator
{
    public PatternPropertiesSyntaxValidator()
    {
        super("patternProperties", NodeType.OBJECT);
    }

    @Override
    public void validate(final ValidationState state, final JsonNode schema)
    {
        super.validate(state, schema);

        if (state.isFailure())
            return;

        final JsonNode node = schema.get(fieldName);
        final Map<String, JsonNode> map
            = CollectionUtils.toMap(node.getFields());

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            if (!RhinoHelper.regexIsValid(entry.getKey()))
                state.addMessage("patternProperties: invalid regex "
                    + entry.getKey());
            if (!entry.getValue().isObject())
                state.addMessage("non schema value in patternProperties");
        }
    }
}
