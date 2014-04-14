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

package com.github.fge.jsonschema.keyword.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.exceptions.InvalidInstanceException;
import com.github.fge.jsonschema.processors.data.FullData;
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
