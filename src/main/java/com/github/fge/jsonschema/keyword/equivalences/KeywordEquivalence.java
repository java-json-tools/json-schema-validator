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

package com.github.fge.jsonschema.keyword.equivalences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public abstract class KeywordEquivalence
    extends Equivalence<JsonNode>
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    protected final String keyword;
    protected final Set<String> retained;

    protected KeywordEquivalence(final String keyword, final String... other)
    {
        this.keyword = keyword;
        retained = ImmutableSet.<String>builder().add(keyword)
            .add(other).build();
    }

    @Override
    protected final boolean doEquivalent(final JsonNode a, final JsonNode b)
    {
        return digestedNode(a).equals(digestedNode(b));
    }

    @Override
    protected final int doHash(final JsonNode t)
    {
        return digestedNode(t).hashCode();
    }

    @Override
    public final String toString()
    {
        return "keyword equivalence for " + keyword
            + " (used keywords: " + retained + ')';
    }

    protected abstract JsonNode digestedNode(final JsonNode orig);
}
