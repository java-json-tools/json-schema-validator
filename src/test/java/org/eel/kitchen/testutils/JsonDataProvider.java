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

package org.eel.kitchen.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

public final class JsonDataProvider
{
    @DataProvider
    public static Iterator<Object[]> getData(final Method m)
        throws IOException
    {
        if (m == null)
            throw new IllegalArgumentException("method must not be null");

        final DataProviderArguments args
            = m.getAnnotation(DataProviderArguments.class);

        if (args == null)
            throw new IllegalArgumentException("No arguments passed");

        if (args.fileName() == null)
            throw new IllegalArgumentException("No annotation data");

        final String resourceName = args.fileName();
        final JsonNode node = JsonLoader.fromResource(resourceName);
        final Iterator<JsonNode> iterator = node.iterator();

        return new Iterator<Object[]>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public Object[] next()
            {
                return new Object[] { iterator.next() };
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }
}
