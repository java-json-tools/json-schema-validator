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

package com.github.fge.jsonschema.keyword.digest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;

import java.util.EnumSet;

/**
 * Base abstract digester class for all keyword digesters
 */
public abstract class AbstractDigester
    implements Digester
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private final EnumSet<NodeType> types;
    protected final String keyword;

    protected AbstractDigester(final String keyword, final NodeType first,
        final NodeType... other)
    {
        this.keyword = keyword;
        types = EnumSet.of(first, other);
    }

    @Override
    public final EnumSet<NodeType> supportedTypes()
    {
        return EnumSet.copyOf(types);
    }

    @Override
    public final String toString()
    {
        return "digester for keyword \"" + keyword + '"';
    }
}
