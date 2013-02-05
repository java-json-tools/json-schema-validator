/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.keyword.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class UniqueItemKeywordValidator
    extends AbstractKeywordValidator
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private final boolean uniqueItems;

    public UniqueItemKeywordValidator(final JsonNode schema)
    {
        super("uniqueItems");
        uniqueItems = schema.get(keyword).booleanValue();
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        if (!uniqueItems)
            return;

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();
        final JsonNode node = data.getInstance().getCurrentNode();

        for (final JsonNode element: node)
            if (!set.add(EQUIVALENCE.wrap(element))) {
                report.error(newMsg(data).msg(ELEMENTS_NOT_UNIQUE));
                return;
            }
    }

    @Override
    public String toString()
    {
        return keyword + ": " + uniqueItems;
    }
}
