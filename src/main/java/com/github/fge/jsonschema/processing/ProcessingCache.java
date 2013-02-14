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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

public final class ProcessingCache<K, V>
{
    private final LoadingCache<Equivalence.Wrapper<K>, V> cache;
    private final Equivalence<K> equivalence;

    public ProcessingCache(final Equivalence<K> equivalence,
        final CacheLoader<Equivalence.Wrapper<K>, V> cacheLoader)
    {
        this.equivalence = equivalence;
        cache = CacheBuilder.newBuilder().build(cacheLoader);
    }

    public V get(final K key)
        throws ProcessingException
    {
        try {
            return cache.get(equivalence.wrap(key));
        } catch (ExecutionException e) {
            throw (ProcessingException) e.getCause();
        }
    }

    public V getUnchecked(final K key)
    {
        return cache.getUnchecked(equivalence.wrap(key));
    }

    @Override
    public String toString()
    {
        return cache.stats().toString();
    }
}
