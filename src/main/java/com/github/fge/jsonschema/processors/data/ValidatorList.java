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

package com.github.fge.jsonschema.processors.data;

import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Output of {@link ValidatorBuilder}, and input/output of {@link
 * FormatProcessor}
 */
public final class ValidatorList
    implements Iterable<KeywordValidator>, MessageProvider
{
    private final List<KeywordValidator> validators;
    private final SchemaContext context;

    public ValidatorList(final SchemaContext context,
        final Collection<KeywordValidator> validators)
    {
        this.context = context;
        this.validators = ImmutableList.copyOf(validators);
    }

    public SchemaContext getContext()
    {
        return context;
    }

    @Override
    public Iterator<KeywordValidator> iterator()
    {
        return validators.iterator();
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return context.newMessage();
    }
}
