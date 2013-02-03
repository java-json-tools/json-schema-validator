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

package com.github.fge.jsonschema.old.keyword.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.old.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.github.fge.jsonschema.validator.ValidationContext;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * Validator for the {@code properties} keyword
 *
 * <p>Again, it should be reminded that this only handles validation at the
 * instance level: this keyword will not validate children nodes.</p>
 *
 * <p>The particular item being validated is {@code required} in subschemas: if
 * this keyword is present, it means that the object instance must have a
 * property by that name.</p>
 */
public final class DraftV3PropertiesKeywordValidator
    extends KeywordValidator
{
    private final Set<String> required;

    public DraftV3PropertiesKeywordValidator(final JsonNode schema)
    {
        super("properties", NodeType.OBJECT);

        final Map<String, JsonNode> map
            = JacksonUtils.asMap(schema.get(keyword));
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            if (entry.getValue().path("required").asBoolean(false))
                builder.add(entry.getKey());

        required = builder.build();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final Set<String> fields = Sets.newHashSet(instance.fieldNames());
        final Set<String> missing = Sets.difference(required, fields);

        if (missing.isEmpty())
            return;

        final Message.Builder msg = newMsg()
            .addInfo("required", Ordering.natural().sortedCopy(required))
            .addInfo("missing", Ordering.natural().sortedCopy(missing))
            .setMessage("required property(ies) not found");
        report.addMessage(msg.build());
    }

    @Override
    public boolean alwaysTrue()
    {
        return required.isEmpty();
    }

    @Override
    public String toString()
    {
        return keyword + ": " +
            (required.isEmpty() ? "none" : required.size()) + " required";
    }
}
