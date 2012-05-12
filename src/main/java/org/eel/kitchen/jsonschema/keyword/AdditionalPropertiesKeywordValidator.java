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
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.NodeType;
import org.eel.kitchen.util.RhinoHelper;

import java.util.HashSet;
import java.util.Set;

public final class AdditionalPropertiesKeywordValidator
    extends KeywordValidator
{
    private final boolean additionalOK;
    private final Set<String> properties = new HashSet<String>();
    private final Set<String> patternProperties = new HashSet<String>();

    public AdditionalPropertiesKeywordValidator(final JsonNode schema)
    {
        super(NodeType.OBJECT);
        additionalOK = schema.get("additionalProperties").asBoolean(true);

        if (additionalOK)
            return;

        if (schema.has("properties"))
            properties.addAll(CollectionUtils.toSet(schema.get("properties")
                .fieldNames()));

        if (schema.has("patternProperties"))
            patternProperties.addAll(CollectionUtils.toSet(schema
                .get("patternProperties").fieldNames()));
    }
    @Override
    public void validate(final ValidationReport report,
        final JsonNode instance)
    {
        if (additionalOK)
            return;

        final Set<String> fields = CollectionUtils.toSet(instance.fieldNames());

        fields.removeAll(properties);

        final Set<String> tmp = new HashSet<String>();

        for (final String field: fields)
            for (final String regex: patternProperties)
                if (RhinoHelper.regMatch(regex, field))
                    tmp.add(field);

        fields.removeAll(tmp);

        if (!fields.isEmpty())
            report.addMessage("additional properties not permitted");
    }
}
