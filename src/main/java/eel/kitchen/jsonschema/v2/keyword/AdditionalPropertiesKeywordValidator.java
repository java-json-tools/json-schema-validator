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

import eel.kitchen.jsonschema.v2.schema.ValidationState;
import eel.kitchen.util.CollectionUtils;
import eel.kitchen.util.RhinoHelper;
import org.codehaus.jackson.JsonNode;

import java.util.HashSet;
import java.util.Set;

public final class AdditionalPropertiesKeywordValidator
    extends AbstractKeywordValidator
{
    private final boolean shortcut;

    private final Set<String> properties = new HashSet<String>();

    private final Set<String> patterns = new HashSet<String>();

    public AdditionalPropertiesKeywordValidator(final JsonNode schema)
    {
        super(schema);

        shortcut = schema.get("additionalProperties").asBoolean(true);

        JsonNode node;

        if (schema.has("properties")) {
            node = schema.get("properties");
            properties.addAll(CollectionUtils.toSet(node.getFieldNames()));
        }

        if (schema.has("patternProperties")) {
            node = schema.get("patternProperties");
            patterns.addAll(CollectionUtils.toSet(node.getFieldNames()));
        }
    }

    @Override
    public void validate(final ValidationState state, final JsonNode node)
    {
        if (shortcut)
            return;

        final Set<String> fields = CollectionUtils.toSet(node.getFieldNames());

        fields.removeAll(properties);

        if (fields.isEmpty())
            return;

        for (final String field: fields) {
            if (patternsMatch(field))
                continue;
            state.addMessage("found property " + field + ", "
                + "but no additional properties are permitted");
            return;
        }

    }

    private boolean patternsMatch(final String field)
    {
        for (final String regex: patterns)
            if (RhinoHelper.regMatch(regex, field))
                return true;

        return false;
    }
}
