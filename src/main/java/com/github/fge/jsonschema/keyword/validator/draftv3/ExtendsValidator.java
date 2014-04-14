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
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;

/**
 * Keyword validator for draft v3's {@code extends}
 */
public final class ExtendsValidator
    extends AbstractKeywordValidator
{
    public ExtendsValidator(final JsonNode digest)
    {
        super("extends");
    }

    @Override
    public void validate(final Processor<FullData, FullData> processor,
        final ProcessingReport report, final MessageBundle bundle,
        final FullData data)
        throws ProcessingException
    {
        final SchemaTree tree = data.getSchema();
        final JsonNode node = tree.getNode().get(keyword);

        FullData newData;

        if (node.isObject()) {
            newData = data.withSchema(tree.append(JsonPointer.of(keyword)));
            processor.process(report, newData);
            return;
        }

        /*
         * Not an object? An array
         */
        final int size = node.size();
        JsonPointer pointer;

        for (int index = 0; index < size; index++) {
            pointer = JsonPointer.of(keyword, index);
            newData = data.withSchema(tree.append(pointer));
            processor.process(report, newData);
        }
    }

    @Override
    public String toString()
    {
        return keyword;
    }

}
