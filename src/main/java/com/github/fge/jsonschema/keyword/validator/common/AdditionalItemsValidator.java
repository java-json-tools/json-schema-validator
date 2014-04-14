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
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

/**
 * Keyword validator for {@code additionalItems}
 */
public final class AdditionalItemsValidator
    extends AbstractKeywordValidator
{
    private final boolean additionalOK;
    private final int itemsSize;

    public AdditionalItemsValidator(final JsonNode digest)
    {
        super("additionalItems");
        additionalOK = digest.get(keyword).booleanValue();
        itemsSize = digest.get("itemsSize").intValue();
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException
    {
        if (additionalOK)
            return;

        final int size = data.getInstance().getNode().size();
        if (size > itemsSize)
            report.error(newMsg(data, bundle,
                "err.common.additionalItems.notAllowed")
                .putArgument("allowed", itemsSize).putArgument("found", size));
    }

    @Override
    public String toString()
    {
        return keyword + ": "
            + (additionalOK ? "allowed" : itemsSize + " max");
    }
}
