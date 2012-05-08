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
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.jsonschema.schema.ValidationReport;
import org.eel.kitchen.util.NodeType;

import java.util.HashSet;
import java.util.Set;

public final class ExtendsKeywordValidator
    extends KeywordValidator
{
    private final Set<JsonNode> schemas = new HashSet<JsonNode>();

    public ExtendsKeywordValidator(final JsonNode schema)
    {
        super(NodeType.values());
        final JsonNode node = schema.get("extends");

        if (node.isObject()) {
            schemas.add(node);
            return;
        }

        for (final JsonNode element: node)
            schemas.add(element);
    }

    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        for (final JsonNode schema: schemas)
            JsonSchema.fromNode(report.getSchema(), schema)
                .validate(report, instance);
    }
}
