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

package com.github.fge.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JsonNodeEquivalence;
import com.github.fge.jsonschema.validator.ValidationContext;
import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Validator for the {@code enum} keyword
 *
 * <p>Note: validation of numeric array elements conform to the definition of
 * equality by JSON Schema. That is, {@code 1} and {@code 1.0} are considered
 * equal.</p>
 */
public final class EnumKeywordValidator
    extends KeywordValidator
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNodeEquivalence.getInstance();

    private final JsonNode enumNode;
    private final Set<Equivalence.Wrapper<JsonNode>> enumValues;

    public EnumKeywordValidator(final JsonNode schema)
    {
        super("enum", NodeType.values());
        enumNode = schema.get(keyword);

        final ImmutableSet.Builder<Equivalence.Wrapper<JsonNode>> builder
            = ImmutableSet.builder();

        for (final JsonNode value: enumNode)
            builder.add(EQUIVALENCE.wrap(value));

        enumValues = builder.build();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (enumValues.contains(EQUIVALENCE.wrap(instance)))
            return;

        final Message.Builder msg = newMsg().addInfo("value", instance)
            .addInfo("enum", enumNode).setMessage("value not found in enum");
        report.addMessage(msg.build());
    }

    @Override
    public String toString()
    {
        /*
         * Enum values may be arbitrarily complex: we therefore choose to only
         * print the number of possible values instead of each possible value.
         *
         * By virtue of syntax validation, we also know that enumValues will
         * never be empty.
         */
        return keyword + ": " + enumValues.size() + " possible value(s)";
    }
}
