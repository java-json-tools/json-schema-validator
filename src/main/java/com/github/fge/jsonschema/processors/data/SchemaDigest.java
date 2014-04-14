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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Output of {@link SchemaDigester} and input of {@link ValidatorBuilder}
 *
 * <p>It bundles a {@link SchemaContext} and a map of digested nodes for keyword
 * construction.</p>
 */
public final class SchemaDigest
    implements MessageProvider
{
    private final SchemaContext context;
    private final Map<String, JsonNode> digested;

    public SchemaDigest(final SchemaContext context,
        final Map<String, JsonNode> map)
    {
        this.context = context;
        digested = ImmutableMap.copyOf(map);
    }

    public SchemaContext getContext()
    {
        return context;
    }

    public Map<String, JsonNode> getDigests()
    {
        return digested;
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return context.newMessage();
    }
}
