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

package com.github.fge.jsonschema.walk;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.report.ProcessingMessage;
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
{
    protected SchemaTree tree;

    private final Map<String, PointerCollector> collectors;

    protected SchemaWalker(final Dictionary<PointerCollector> dict,
        final SchemaTree tree)
    {
        collectors = dict.entries();
        this.tree = tree;
    }

    public final void walk(final SchemaListener listener,
        final ProcessingReport report)
        throws ProcessingException
    {
        report.debug(
            new ProcessingMessage().message("entering tree").put("tree", tree));
        listener.onInit(tree);
        doWalk(listener, report);
        report.debug(
            new ProcessingMessage().message("exiting tree").put("tree", tree));
        listener.onExit();
    }

    public abstract void resolveTree(final SchemaListener listener,
        final ProcessingReport report)
        throws ProcessingException;

    private void doWalk(final SchemaListener listener,
        final ProcessingReport report)
        throws ProcessingException
    {
        report.debug(new ProcessingMessage().message("walking tree")
            .put("tree", tree));
        listener.onWalk(tree);
        resolveTree(listener, report);
        final JsonNode node = tree.getNode();

        final Map<String, PointerCollector> map = Maps.newTreeMap();
        map.putAll(collectors);

        map.keySet().retainAll(Sets.newHashSet(node.fieldNames()));

        /*
         * Collect pointers for further processing.
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final PointerCollector collector: map.values())
            collector.collect(pointers, tree);

        /*
         * Operate on these pointers.
         */
        SchemaTree current;
        for (final JsonPointer pointer: pointers) {
            current = tree;
            tree = tree.append(pointer);
            listener.onPushd(pointer);
            doWalk(listener, report);
            listener.onPopd();
            tree = current;
        }
    }

    @Override
    public abstract String toString();
}
