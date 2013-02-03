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

package com.github.fge.jsonschema.keyword;

import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.List;

public final class KeywordSet
    implements Iterable<KeywordValidator>, MessageProvider
{
    private final List<KeywordValidator> validators;

    public KeywordSet(final List<KeywordValidator> validators)
    {
        this.validators = ImmutableList.copyOf(validators);
    }

    @Override
    public Iterator<KeywordValidator> iterator()
    {
        return validators.iterator();
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return new ProcessingMessage();
    }
}
