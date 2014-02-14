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

package com.github.fge.jsonschema.keyword.validator.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Keyword validator for draft v4's {@code required}
 */
public final class RequiredKeywordValidator
    extends AbstractKeywordValidator
{
    private final Set<String> required;

    public RequiredKeywordValidator(final JsonNode digest)
    {
        super("required");
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (final JsonNode element: digest.get(keyword))
            builder.add(element.textValue());

        required = builder.build();
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException
    {
        final Set<String> set = Sets.newLinkedHashSet(required);
        set.removeAll(Sets.newHashSet(data.getInstance().getNode()
            .fieldNames()));

        if (!set.isEmpty())
            report.error(newMsg(data, bundle, "err.common.object.missingMembers")
                .put("required", required)
                .putArgument("missing", toArrayNode(set)));
    }

    @Override
    public String toString()
    {
        return keyword + ": " + required.size() + " properties";
    }
}
