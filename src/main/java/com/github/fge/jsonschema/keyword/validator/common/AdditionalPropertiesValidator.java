/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.validator.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.RhinoHelper;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Keyword validator for {@code additionalProperties}
 */
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
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
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
        final ArrayNode node = JacksonUtils.nodeFactory().arrayNode();
        for (final String field: Ordering.natural().sortedCopy(fields))
            node.add(field);
        report.error(newMsg(data, bundle,
            "err.common.additionalProperties.notAllowed")
            .putArgument("unwanted", node));
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
