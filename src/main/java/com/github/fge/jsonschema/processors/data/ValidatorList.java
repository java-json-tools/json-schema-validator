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

package com.github.fge.jsonschema.processors.data;

import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
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
