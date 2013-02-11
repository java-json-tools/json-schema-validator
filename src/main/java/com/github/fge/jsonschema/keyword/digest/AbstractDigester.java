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

package com.github.fge.jsonschema.keyword.digest;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.util.Digester;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.NodeType;

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
