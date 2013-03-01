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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public final class SchemaWalkerTest
{
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final SchemaTree SCHEMA;
    private static final SchemaTree SCHEMA2;

    static {
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put(K1, K1);
        schema.put(K2, K2);
        SCHEMA = new CanonicalSchemaTree(schema);
        SCHEMA2 = new CanonicalSchemaTree(schema);
    }

    private PointerCollector collector1;
    private PointerCollector collector2;
    private ProcessingReport report;
    private SchemaWalker walker;
    private SchemaHolder input;

    @BeforeMethod
    public void initCollectors()
    {
        collector1 = mock(PointerCollector.class);
        collector2 = mock(PointerCollector.class);
        report = mock(ProcessingReport.class);
        input = new SchemaHolder(SCHEMA);
    }

    @Test
    public void onlyRelevantCollectorsAreUsed()
        throws ProcessingException
    {
        final DictionaryBuilder<PointerCollector> dict
            = Dictionary.newBuilder();

        dict.addEntry(K1, collector1);

        walker = new TestWalker(dict.freeze(), SCHEMA);
        walker.process(report, input);
        verify(collector1, only())
            .collect(anyCollectionOf(JsonPointer.class), same(SCHEMA));
        verifyZeroInteractions(collector2);
    }

    @Test
    public void allRelevantCollectorsAreUsed()
        throws ProcessingException
    {
        final DictionaryBuilder<PointerCollector> dict
            = Dictionary.newBuilder();

        dict.addEntry(K1, collector1).addEntry(K2, collector2);

        walker = new TestWalker(dict.freeze(), SCHEMA);
        walker.process(report, input);
        verify(collector1, only())
            .collect(anyCollectionOf(JsonPointer.class), same(SCHEMA));
        verify(collector2, only())
            .collect(anyCollectionOf(JsonPointer.class), same(SCHEMA));
    }

    @Test
    public void whenNewTreeIsReturnedOldTreeIsNotWalked()
        throws ProcessingException
    {
        final DictionaryBuilder<PointerCollector> dict
            = Dictionary.newBuilder();

        dict.addEntry(K1, collector1).addEntry(K2, collector2);

        walker = new TestWalker(dict.freeze(), SCHEMA2);
        walker.process(report, input);
        verify(collector1, only())
            .collect(anyCollectionOf(JsonPointer.class), same(SCHEMA2));
        verify(collector2, only())
            .collect(anyCollectionOf(JsonPointer.class), same(SCHEMA2));

    }

    private static final class TestWalker
        extends SchemaWalker
    {
        private final SchemaTree newTree;

        private TestWalker(final Dictionary<PointerCollector> dict,
            final SchemaTree tree)
        {
            super(dict);
            newTree = tree;
        }

        @Override
        public SchemaTree processCurrent(final ProcessingReport report,
            final SchemaTree tree)
            throws ProcessingException
        {
            return newTree;
        }

        @Override
        public String toString()
        {
            return "whatever";
        }
    }
}