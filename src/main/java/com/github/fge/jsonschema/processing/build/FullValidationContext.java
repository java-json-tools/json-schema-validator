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

package com.github.fge.jsonschema.processing.build;

import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class FullValidationContext
    implements Iterable<KeywordValidator>, MessageProvider
{
    private final List<KeywordValidator> validators;
    private final ValidationData validationData;

    public FullValidationContext(final ValidationData validationData,
        final Collection<KeywordValidator> validators)
    {
        this.validationData = validationData;
        this.validators = ImmutableList.copyOf(validators);
    }

    public ValidationData getValidationData()
    {
        return validationData;
    }

    @Override
    public Iterator<KeywordValidator> iterator()
    {
        return validators.iterator();
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return validationData.newMessage();
    }
}
