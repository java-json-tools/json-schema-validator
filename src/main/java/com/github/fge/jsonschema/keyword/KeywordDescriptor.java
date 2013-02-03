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

package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.util.Frozen;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.ProcessingCache;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

public final class KeywordDescriptor
    implements Frozen<KeywordDescriptorBuilder>
{
    final Constructor<? extends KeywordValidator> constructor;
    final EnumSet<NodeType> types;
    final Equivalence<JsonNode> equivalence;

    public static KeywordDescriptorBuilder newBuilder()
    {
        return new KeywordDescriptorBuilder();
    }

    KeywordDescriptor(final KeywordDescriptorBuilder builder)
    {
        constructor = builder.constructor;
        types = EnumSet.copyOf(builder.types);
        equivalence = builder.equivalence;
    }

    ProcessingCache<JsonNode, KeywordValidator> buildCache()
    {
        final CacheLoader<Equivalence.Wrapper<JsonNode>, KeywordValidator> load
            = new CacheLoader<Equivalence.Wrapper<JsonNode>, KeywordValidator>()
        {
            @Override
            public KeywordValidator load(final Equivalence.Wrapper<JsonNode> key)
                throws ProcessingException
            {
                try {
                    return constructor.newInstance(key.get());
                } catch (InstantiationException e) {
                    throw new ProcessingException(
                        "failed to instansiate validator", e);
                } catch (IllegalAccessException e) {
                    throw new ProcessingException(
                        "permission problem, cannot instansiate", e);
                } catch (InvocationTargetException e) {
                    throw new ProcessingException(
                        "failed to invoke constructor for validator", e);
                }
            }
        };

        return new ProcessingCache<JsonNode, KeywordValidator>(equivalence,
            load);
    }
    @Override
    public KeywordDescriptorBuilder thaw()
    {
        return new KeywordDescriptorBuilder(this);
    }
}
