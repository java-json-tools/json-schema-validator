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

package com.github.fge.jsonschema.keyword.validator.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.google.common.collect.Lists;

import java.util.EnumSet;
import java.util.List;

/**
 * Helper keyword validator for draft v3's {@code type} and {@code disallow}
 *
 * <p>Their validation logic differ, however their digest is the same; therefore
 * they are built in the same way.</p>
 */
public abstract class DraftV3TypeKeywordValidator
    extends AbstractKeywordValidator
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    protected final EnumSet<NodeType> types = EnumSet.noneOf(NodeType.class);
    protected final List<Integer> schemas = Lists.newArrayList();

    protected DraftV3TypeKeywordValidator(final String keyword,
        final JsonNode digested)
    {
        super(keyword);
        for (final JsonNode element: digested.get(keyword))
            types.add(NodeType.fromName(element.textValue()));
        for (final JsonNode element: digested.get("schemas"))
            schemas.add(element.intValue());
    }

    @Override
    public final String toString()
    {
        return keyword + ": " + types + "; " + schemas.size() + " schemas";
    }
}
