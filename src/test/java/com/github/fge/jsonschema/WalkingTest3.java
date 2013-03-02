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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processors.refexpand.MutableTree;
import com.github.fge.jsonschema.walk.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.PointerCollector;
import com.github.fge.jsonschema.walk.RecursiveSchemaWalker;
import com.github.fge.jsonschema.walk.SchemaListener;
import com.github.fge.jsonschema.walk.SchemaWalker;
import com.github.fge.jsonschema.report.ConsoleProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;

public final class WalkingTest3
{
    private WalkingTest3()
    {
    }

    public static void main(final String... args)
        throws ProcessingException, IOException
    {
        final JsonNode schema = JsonLoader.fromResource("/main.json");
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .preloadSchema(schema)
            .preloadSchema(JsonLoader.fromResource("/sub1.json"))
            .preloadSchema(JsonLoader.fromResource("/sub2.json")).freeze();
        final ProcessingReport report = new ConsoleProcessingReport(
            LogLevel.DEBUG, LogLevel.FATAL);
        final MutableTree tree = new MutableTree();
        final SchemaListener listener = new SchemaExpander(tree);
        final Dictionary<PointerCollector> dict
            = DraftV4PointerCollectorDictionary.get();
        final SchemaWalker walker = new RecursiveSchemaWalker(dict,
            new CanonicalSchemaTree(schema), cfg);

        walker.walk(listener, report);
        System.out.println(JacksonUtils.prettyPrint(tree.getBaseNode()));
    }

    private static final class SchemaExpander
        implements SchemaListener
    {
        private final MutableTree mutableTree;

        private SchemaExpander(final MutableTree mutableTree)
        {
            this.mutableTree = mutableTree;
        }

        @Override
        public void onInit(final SchemaTree tree)
        {
            mutableTree.setCurrentNode(tree.getNode());
        }

        @Override
        public void onWalk(final SchemaTree tree)
        {
        }

        @Override
        public void onPopd()
        {
            mutableTree.pop();
        }

        @Override
        public void onNewTree(final SchemaTree oldTree,
            final SchemaTree newTree)
        {
            mutableTree.setCurrentNode(newTree.getNode());
        }

        @Override
        public void onPushd(final JsonPointer pointer)
        {
            mutableTree.pushd(pointer);
        }

        @Override
        public void onExit()
        {
        }

        public JsonNode getNode()
        {
            return mutableTree.getBaseNode();
        }
    }
}
