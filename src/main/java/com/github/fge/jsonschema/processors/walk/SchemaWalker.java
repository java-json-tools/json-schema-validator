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
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

/*
 * NOTE NOTE NOTE: the schema MUST be valid at this point
 */
public final class SchemaWalker
    implements Processor<SchemaHolder, SchemaHolder>
{
    private final Map<String, KeywordWalker> walkers;

    public SchemaWalker(final Dictionary<KeywordWalker> dict)
    {
        walkers = dict.entries();
    }

    @Override
    public SchemaHolder process(final ProcessingReport report,
        final SchemaHolder input)
        throws ProcessingException
    {
        walk(report, input.getValue());
        return input;
    }

    private void walk(final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = tree.getNode();

        /*
         * Grab all walkers and object member names. Retain walkers only for
         * registered keywords, and remove from the member names set what is in
         * the walkers' key set: if non empty, some walkers are missing (on
         * purpose or not), report them.
         */
        final Map<String, KeywordWalker> map = Maps.newTreeMap();
        map.putAll(walkers);

        final Set<String> fieldNames = Sets.newHashSet(node.fieldNames());
        map.keySet().retainAll(fieldNames);
        fieldNames.removeAll(map.keySet());

        if (!fieldNames.isEmpty())
            report.warn(newMsg(tree).message(UNKNOWN_KEYWORDS)
                .put("ignored", Ordering.natural().sortedCopy(fieldNames)));

        /*
         * Now, walk each keyword, and collect pointers for further processing.
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final KeywordWalker walker: map.values())
            walker.walk(pointers, report, tree);

        /*
         * Operate on these pointers.
         */
        for (final JsonPointer pointer: pointers)
            walk(report, tree.append(pointer));
    }

    private static ProcessingMessage newMsg(final SchemaTree tree)
    {
        return new ProcessingMessage().put("schema", tree);
    }

    @Override
    public String toString()
    {
        return "schema waler";
    }
}
