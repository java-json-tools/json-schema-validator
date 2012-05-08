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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.schema.JsonSchema;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DependenciesKeywordValidator
    extends KeywordValidator
{
    private final Map<String, Set<String>> simple
        = new HashMap<String, Set<String>>();

    private final Map<String, JsonNode> schemas
        = new HashMap<String, JsonNode>();

    public DependenciesKeywordValidator(final JsonNode schema)
    {
        super(NodeType.OBJECT);
        final Map<String, JsonNode> fields
            = CollectionUtils.toMap(schema.get("dependencies").fields());

        String key;
        JsonNode value;
        for (final Map.Entry<String, JsonNode> entry: fields.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value.isObject())
                schemas.put(key, value);
            else
                simple.put(key, simpleDepdency(value));
        }
    }

    private Set<String> simpleDepdency(final JsonNode value)
    {
        final Set<String> ret = new HashSet<String>();

        /*
         * This works: for non container values, an empty iterator is
         * returned. And we can only be called from here if the dependencies
         * syntax is correct, so it's either an array...
         */
        for (final JsonNode tmp: value)
            ret.add(tmp.textValue());

        /*
         * Or a string value.
         */
        if (ret.isEmpty())
            ret.add(value.textValue());

        return ret;
    }


    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        final Set<String> fields = CollectionUtils.toSet(instance.fieldNames());

        final Map<String, Set<String>> simpleDeps
            = new HashMap<String, Set<String>>(simple);

        simpleDeps.keySet().retainAll(fields);

        final Set<String> fullSet = new HashSet<String>();

        for (final Set<String> set: simpleDeps.values())
            fullSet.addAll(set);

        if (!fields.containsAll(fullSet))
            report.addMessage("missing property dependencies");

        final Map<String, JsonNode> schemaDeps
            = new HashMap<String, JsonNode>(schemas);

        schemaDeps.keySet().retainAll(fields);

        for (final JsonNode node: schemaDeps.values())
            JsonSchema.fromNode(report.getSchema(), node)
                .validate(report, instance);
    }
}
