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

package com.github.fge.jsonschema.processors.walk.helpers;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.collect.Lists;

public abstract class SchemaMapPointerCollector
    extends AbstractPointerCollector
{
    protected SchemaMapPointerCollector(String keyword) {
        super(keyword);
    }

    @Override
    public void collect(final Collection<JsonPointer> pointers,
        final SchemaTree tree)
    {
        final List<String> fieldNames
            = Lists.newArrayList(getNode(tree).fieldNames());
        Collections.sort(fieldNames);
        for (final String fieldName: fieldNames)
            pointers.add(basePointer.append(fieldName));
    }
}
