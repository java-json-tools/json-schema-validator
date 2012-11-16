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

package org.eel.kitchen.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Validator for the (draft v4) {@code required} keyword
 *
 * <p>In draft v3, this keyword was attached to a schema defined by the {@code
 * properties} keyword. In draft v4, it is a "first-level" citizen. Its value
 * is a set of property names which the object instance must contain in order
 * to validate successfully.</p>
 */
public final class RequiredKeywordValidator
    extends KeywordValidator
{
    private final Set<String> required;

    public RequiredKeywordValidator(final JsonNode schema)
    {
        super("required", NodeType.OBJECT);

        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (final JsonNode element: schema.get(keyword))
            builder.add(element.textValue());

        required = builder.build();
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final HashSet<String> fields = Sets.newHashSet(instance.fieldNames());
        final Set<String> missing = Sets.difference(required, fields);

        if (missing.isEmpty())
            return;

        final Message.Builder msg = newMsg()
            .setMessage("required property(ies) not found")
            .addInfo("required", Ordering.natural().sortedCopy(required))
            .addInfo("missing", Ordering.natural().sortedCopy(missing));

        report.addMessage(msg.build());

    }

    @Override
    public String toString()
    {
        return "required: " + Ordering.natural().sortedCopy(required);
    }
}
