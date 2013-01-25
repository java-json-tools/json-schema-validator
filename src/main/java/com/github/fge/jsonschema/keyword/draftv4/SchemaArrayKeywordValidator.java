/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Abstract class for all keywords taking a schema array as a value
 *
 * <p>This covers {@code anyOf}, {@code allOf} and {@code oneOf}.</p>
 */
public abstract class SchemaArrayKeywordValidator
    extends KeywordValidator
{
    protected final Set<JsonNode> subSchemas;

    /**
     * Constructor
     */
    protected SchemaArrayKeywordValidator(final String keyword,
        final JsonNode schema)
    {
        super(keyword, NodeType.values());
        subSchemas = ImmutableSet.copyOf(schema.get(keyword));
    }
}
