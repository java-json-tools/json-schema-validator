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

package com.github.fge.jsonschema.keyword.validator.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Keyword validator for draft v3's {@code properties}
 */
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
        return "required: " + required.size() + " properties";
    }
}
