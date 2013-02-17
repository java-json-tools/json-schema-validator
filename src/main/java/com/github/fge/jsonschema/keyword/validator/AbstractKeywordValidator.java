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

package com.github.fge.jsonschema.keyword.validator;

import com.github.fge.jsonschema.processors.ValidationDomain;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingMessage;

public abstract class AbstractKeywordValidator
    implements KeywordValidator
{
    protected final String keyword;

    protected AbstractKeywordValidator(final String keyword)
    {
        this.keyword = keyword;
    }

    protected final ProcessingMessage newMsg(final FullData data)
    {
        return data.newMessage().put("domain", "validation")
            .put("keyword", keyword)
            .setExceptionProvider(ValidationDomain.INSTANCE.exceptionProvider());
    }

    @Override
    public abstract String toString();
}
