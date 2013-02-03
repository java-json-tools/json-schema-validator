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
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.Thawed;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;

import java.util.EnumSet;

public final class KeywordBuilder
    implements Thawed<Keyword>
{
    /**
     * The name of this keyword
     */
    final String name;

    /**
     * Its syntax validator
     */
    SyntaxChecker syntaxChecker;

    /**
     * Its validator class
     */
    Class<? extends KeywordValidator> validatorClass;

    /**
     * The types this keyword validates -- all types by default
     */
    EnumSet<NodeType> types = EnumSet.allOf(NodeType.class);

    /**
     * Its schema equivalence checker -- JSON Schema equivalence by default
     */
    Equivalence<JsonNode> equivalence = JsonSchemaEquivalence.getInstance();


    KeywordBuilder(final String name)
    {
        this.name = name;
    }

    KeywordBuilder(final Keyword keyword)
    {
        name = keyword.name;
        syntaxChecker = keyword.syntaxChecker;
        equivalence = keyword.equivalence;
        types = EnumSet.copyOf(keyword.types);
        validatorClass = keyword.validatorClass;
    }

    @Override
    public Keyword freeze()
    {
        return new Keyword(this);
    }
}
