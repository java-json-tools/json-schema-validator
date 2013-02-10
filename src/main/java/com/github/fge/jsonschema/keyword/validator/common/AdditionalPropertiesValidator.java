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
import com.github.fge.jsonschema.util.RhinoHelper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;

public final class AdditionalPropertiesValidator
    extends AbstractKeywordValidator
{
    private static final Joiner TOSTRING_JOINER = Joiner.on("; or ");

    private final boolean additionalOK;
    private final Set<String> properties;
    private final Set<String> patternProperties;

    public AdditionalPropertiesValidator(final JsonNode digest)
    {
        super("additionalProperties");
        additionalOK = digest.get(keyword).booleanValue();

        ImmutableSet.Builder<String> builder;

        builder = ImmutableSet.builder();
        for (final JsonNode node: digest.get("properties"))
            builder.add(node.textValue());
        properties = builder.build();

        builder = ImmutableSet.builder();
        for (final JsonNode node: digest.get("patternProperties"))
            builder.add(node.textValue());
        patternProperties = builder.build();
    }

    @Override
    public void validate(
        final Processor<ValidationData, ProcessingReport> processor,
        final ProcessingReport report, final ValidationData data)
        throws ProcessingException
    {
        if (additionalOK)
            return;

        final JsonNode instance = data.getInstance().getNode();
        final Set<String> fields = Sets.newHashSet(instance.fieldNames());

        fields.removeAll(properties);

        final Set<String> tmp = Sets.newHashSet();

        for (final String field: fields)
            for (final String regex: patternProperties)
                if (RhinoHelper.regMatch(regex, field))
                    tmp.add(field);

        fields.removeAll(tmp);

        if (fields.isEmpty())
            return;

        /*
         * Display extra properties in order in the report
         */
        report.error(newMsg(data).msg(ADDITIONAL_PROPERTIES_NOT_ALLOWED)
            .put("unwanted", Ordering.natural().sortedCopy(fields)));
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(keyword + ": ");

        if (additionalOK)
            return sb.append("allowed").toString();

        sb.append("none");

        if (properties.isEmpty() && patternProperties.isEmpty())
            return sb.toString();

        sb.append(", unless: ");

        final Set<String> further = Sets.newLinkedHashSet();

        if (!properties.isEmpty())
            further.add("one property is any of: " + properties);

        if (!patternProperties.isEmpty())
            further.add("a property matches any regex among: "
                + patternProperties);

        sb.append(TOSTRING_JOINER.join(further));

        return sb.toString();
    }
}
