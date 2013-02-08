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

package com.github.fge.jsonschema.keyword.validator.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Lists;

import java.util.EnumSet;
import java.util.List;

public abstract class DraftV3TypeKeywordValidator
    extends AbstractKeywordValidator
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    protected final JsonPointer basePtr;
    protected final EnumSet<NodeType> types = EnumSet.noneOf(NodeType.class);
    protected final List<Integer> schemas = Lists.newArrayList();

    protected DraftV3TypeKeywordValidator(final String keyword,
        final JsonNode digested)
    {
        super(keyword);
        basePtr = JsonPointer.empty().append(keyword);
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
