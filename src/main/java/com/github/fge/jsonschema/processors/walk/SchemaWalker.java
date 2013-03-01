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

package com.github.fge.jsonschema.processors.walk;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;

/*
 * NOTE NOTE NOTE: the schema MUST be valid at this point
 */
public abstract class SchemaWalker
    implements Processor<SchemaHolder, SchemaHolder>
{
    private final Map<String, PointerCollector> collectors;

    protected SchemaWalker(final Dictionary<PointerCollector> dict)
    {
        collectors = dict.entries();
    }

    @Override
    public final SchemaHolder process(final ProcessingReport report,
        final SchemaHolder input)
        throws ProcessingException
    {
        walk(report, input.getValue());
        return input;
    }

    public abstract SchemaTree processCurrent(final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException;

    private void walk(final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        /*
         * First grab the transformed tree, and operate on it
         */
        final SchemaTree newTree = processCurrent(report, tree);
        final JsonNode node = newTree.getNode();

        final Map<String, PointerCollector> map = Maps.newTreeMap();
        map.putAll(collectors);

        map.keySet().retainAll(Sets.newHashSet(node.fieldNames()));

        /*
         * Collect pointers for further processing.
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final PointerCollector collector: map.values())
            collector.collect(pointers, newTree);

        /*
         * Operate on these pointers.
         */
        for (final JsonPointer pointer: pointers)
            walk(report, newTree.append(pointer));
    }

    @Override
    public abstract String toString();
}
