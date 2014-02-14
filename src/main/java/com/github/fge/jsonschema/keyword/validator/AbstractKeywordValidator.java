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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.exceptions.InvalidInstanceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.Collection;

/**
 * Base abstract class for keyword validators
 *
 * <p>This class provides a template message for error reporting, with all
 * details about the current validation context already filled.</p>
 */
public abstract class AbstractKeywordValidator
    implements KeywordValidator
{
    private static final ExceptionProvider EXCEPTION_PROVIDER
        = new ExceptionProvider()
    {
        @Override
        public ProcessingException doException(final ProcessingMessage message)
        {
            return new InvalidInstanceException(message);
        }
    };

    protected final String keyword;

    /**
     * Protected constructor
     *
     * @param keyword the keyword's name
     */
    protected AbstractKeywordValidator(final String keyword)
    {
        this.keyword = keyword;
    }

    protected final ProcessingMessage newMsg(final FullData data)
    {
        return data.newMessage().put("domain", "validation")
            .put("keyword", keyword)
            .setExceptionProvider(EXCEPTION_PROVIDER);
    }

    protected final ProcessingMessage newMsg(final FullData data,
        final MessageBundle bundle, final String key)
    {
        return data.newMessage().put("domain", "validation")
            .put("keyword", keyword).setMessage(bundle.getMessage(key))
            .setExceptionProvider(EXCEPTION_PROVIDER);
    }

    protected static <T> JsonNode toArrayNode(final Collection<T> collection)
    {
        final ArrayNode node = JacksonUtils.nodeFactory().arrayNode();
        for (final T element: collection)
            node.add(element.toString());
        return node;
    }

    @Override
    public abstract String toString();
}
