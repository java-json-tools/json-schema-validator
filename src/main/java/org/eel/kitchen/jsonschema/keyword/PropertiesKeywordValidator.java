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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.Map;
import java.util.Set;

/**
 * Validator for the {@code properties} keyword
 *
 * <p>Again, it should be reminded that this only handles validation at the
 * instance level: this keyword will not validate children nodes.</p>
 *
 * <p>The particular item being validated is {@code required} in subschemas:
 * if this keyword is present, it means that the object instance must have a
 * property by that name.</p>
 */
public final class PropertiesKeywordValidator
    extends KeywordValidator
{
    private final Set<String> required;

    public PropertiesKeywordValidator(final JsonNode schema)
    {
        super("properties", NodeType.OBJECT);

        final Map<String, JsonNode> map
            = JacksonUtils.nodeToMap(schema.get(keyword));
        final ImmutableSet.Builder<String> builder
            = new ImmutableSet.Builder<String>();

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            if (entry.getValue().path("required").asBoolean(false))
                builder.add(entry.getKey());

        required = builder.build();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final Set<String> fields = JacksonUtils.fieldNames(instance);

        if (fields.containsAll(required))
            return;

        final Set<String> requiredSorted = Sets.newTreeSet(required);
        final Set<String> missing = Sets.newTreeSet(required);
        missing.removeAll(fields);

        final ValidationMessage.Builder msg = newMsg()
            .addInfo("required", requiredSorted).addInfo("missing", missing)
            .setMessage("required property(ies) not found");
        report.addMessage(msg.build());
    }

    @Override
    public String toString()
    {
        return keyword + ": " +
            (required.isEmpty() ? "none" : required.size()) + " required";
    }
}
