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

package com.github.fge.jsonschema.keyword.validator.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class PropertiesValidator
    extends AbstractKeywordValidator
{
    private final Set<String> required;

    public PropertiesValidator(final JsonNode digest)
    {
        super("properties");
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (final JsonNode element: digest.get("required"))
            builder.add(element.textValue());

        required = builder.build();
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        final Set<String> set = Sets.newLinkedHashSet(required);
        set.removeAll(Sets.newHashSet(data.getInstance().getNode()
            .fieldNames()));

        if (!set.isEmpty())
            report.error(newMsg(data).msg(MISSING_REQUIRED_MEMBERS)
                .put("required", required).put("missing", set));
    }

    @Override
    public String toString()
    {
        return "required: " + required.size() + " properties";
    }
}
