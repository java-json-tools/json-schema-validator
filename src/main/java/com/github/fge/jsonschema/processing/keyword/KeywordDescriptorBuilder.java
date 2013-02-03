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

package com.github.fge.jsonschema.processing.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.Thawed;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;
import java.util.EnumSet;

public final class KeywordDescriptorBuilder
    implements Thawed<KeywordDescriptor>
{
    private static final String ERRMSG
        = "class has no appropriate constructor\n\n"
        + "Please provide a constructor with a JsonNode argument\n";

    Constructor<? extends KeywordValidator> constructor;
    EnumSet<NodeType> types = EnumSet.allOf(NodeType.class);
    Equivalence<JsonNode> equivalence = JsonSchemaEquivalence.getInstance();

    KeywordDescriptorBuilder()
    {
    }

    KeywordDescriptorBuilder(final KeywordDescriptor descriptor)
    {
        constructor = descriptor.constructor;
        types = EnumSet.copyOf(descriptor.types);
        equivalence = descriptor.equivalence;
    }

    public KeywordDescriptorBuilder setValidatorClass(
        final Class<? extends KeywordValidator> c)
    {
        Preconditions.checkNotNull(c, "class must not be null");
        try {
            constructor = c.getConstructor(JsonNode.class);
            return this;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(ERRMSG, e);
        }
    }

    public KeywordDescriptorBuilder setValidatedTypes(final NodeType first,
        final NodeType... other)
    {
        Preconditions.checkNotNull(first, "there must not be a null type");
        for (final NodeType type: other)
            Preconditions.checkNotNull(type, "there must not be a null type");
        types = EnumSet.of(first, other);
        return this;
    }

    public KeywordDescriptorBuilder setSchemaEquivalence(
        final Equivalence<JsonNode> equivalence)
    {
        this.equivalence = Preconditions.checkNotNull(equivalence,
            "equivalence must not be null");
        return this;
    }

    @Override
    public KeywordDescriptor freeze()
    {
        return new KeywordDescriptor(this);
    }
}
