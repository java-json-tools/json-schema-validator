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
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Keyword validator for {@code uniqueItems}
 *
 * @see JsonNumEquals
 */
public final class UniqueItemsValidator
    extends AbstractKeywordValidator
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquals.getInstance();

    private final boolean uniqueItems;

    public UniqueItemsValidator(final JsonNode digest)
    {
        super("uniqueItems");
        uniqueItems = digest.get(keyword).booleanValue();
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException
    {
        if (!uniqueItems)
            return;

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();
        final JsonNode node = data.getInstance().getNode();

        for (final JsonNode element: node)
            if (!set.add(EQUIVALENCE.wrap(element))) {
                report.error(newMsg(data, bundle,
                    "err.common.uniqueItems.duplicateElements"));
                return;
            }
    }

    @Override
    public String toString()
    {
        return keyword + ": " + uniqueItems;
    }
}
