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
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.github.fge.jsonschema.validator.ValidationContext;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Validator for the {@code uniqueItems} keyword
 *
 * <p>Note: validation of numeric array elements conform to the definition of
 * equality by JSON Schema. That is, {@code 1} and {@code 1.0} are considered
 * equal.</p>
 */
public final class UniqueItemsKeywordValidator
    extends KeywordValidator
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private final boolean uniqueItems;

    public UniqueItemsKeywordValidator(final JsonNode schema)
    {
        super("uniqueItems", NodeType.ARRAY);
        uniqueItems = schema.get(keyword).booleanValue();
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (!uniqueItems)
            return;

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        for (final JsonNode element: instance)
            if (!set.add(EQUIVALENCE.wrap(element))) {
                final Message.Builder msg = newMsg()
                    .setMessage("duplicate elements in array");
                report.addMessage(msg.build());
                return;
            }
    }

    @Override
    public boolean alwaysTrue()
    {
        return !uniqueItems;
    }

    @Override
    public String toString()
    {
        return keyword + ": " + uniqueItems;
    }
}
