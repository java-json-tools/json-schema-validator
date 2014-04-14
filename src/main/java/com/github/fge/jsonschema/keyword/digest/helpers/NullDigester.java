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

package com.github.fge.jsonschema.keyword.digest.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.digest.AbstractDigester;

/**
 * A digester returning a {@link NullNode} for any input
 *
 * <p>This is actually useful for keywords like {@code anyOf}, {@code allOf} and
 * {@code oneOf}, which only roles are to validate subschemas: they do not need
 * a digested form at all, they just have to peek at the schema.</p>
 *
 * <p>A net result of all keywords using this digester is that only one instance
 * will ever be built.</p>
 */
public final class NullDigester
    extends AbstractDigester
{
    public NullDigester(final String keyword, final NodeType first,
        final NodeType... other)
    {
        super(keyword, first, other);
    }

    @Override
    public JsonNode digest(final JsonNode schema)
    {
        return FACTORY.nullNode();
    }
}
