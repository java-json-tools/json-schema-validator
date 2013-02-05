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

package com.github.fge.jsonschema.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.util.Frozen;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;

import java.util.EnumSet;

public final class Keyword
    implements Frozen<KeywordBuilder>
{
    /**
     * The name of this keyword
     */
    final String name;

    /**
     * Its syntax validator
     */
    final SyntaxChecker syntaxChecker;

    /**
     * Its validator class
     */
    final Class<? extends KeywordValidator> validatorClass;

    /**
     * The types this keyword validates
     */
    final EnumSet<NodeType> types;

    /**
     * Its schema equivalence checker
     */
    final Equivalence<JsonNode> equivalence;


    public static KeywordBuilder newBuilder(final String name)
    {
        Preconditions.checkNotNull(name, "name must not be null");
        return new KeywordBuilder(name);
    }

    Keyword(final KeywordBuilder builder)
    {
        name = Preconditions.checkNotNull(builder.name,
            "keyword must not be null");
        syntaxChecker = builder.syntaxChecker;
        validatorClass = builder.validatorClass;
        types = EnumSet.copyOf(builder.types);
        equivalence = builder.equivalence;
    }

    @Override
    public KeywordBuilder thaw()
    {
        return new KeywordBuilder(this);
    }
}
