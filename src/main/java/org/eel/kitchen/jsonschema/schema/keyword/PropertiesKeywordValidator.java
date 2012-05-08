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

package org.eel.kitchen.jsonschema.schema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.schema.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PropertiesKeywordValidator
    extends KeywordValidator
{
    private final Set<String> required = new HashSet<String>();

    public PropertiesKeywordValidator(final JsonNode schema)
    {
        super(NodeType.OBJECT);
        final Map<String, JsonNode> map
            = CollectionUtils.toMap(schema.get("properties").fields());

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            if (entry.getValue().path("required").asBoolean(false))
                required.add(entry.getKey());
    }

    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        final Set<String> fields = CollectionUtils.toSet(instance.fieldNames());

        if (!fields.containsAll(required))
            report.addMessage("missing required properties in instance");
    }
}
