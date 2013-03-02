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

package com.github.fge.jsonschema.processors.refexpand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.processors.ref.RefResolver;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;
import com.github.fge.jsonschema.processors.validation.SchemaTreeEquivalence;
import com.github.fge.jsonschema.processors.walk.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.processors.walk.SchemaWalker;
import com.github.fge.jsonschema.report.DevNullProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableList;

import java.io.IOException;

public final class RefExpander
    extends SchemaWalker
{
    private static final Equivalence<SchemaTree> EQUIVALENCE
        = SchemaTreeEquivalence.getInstance();

    private final Processor<SchemaHolder, SchemaHolder> refSyntax;

    public RefExpander(final LoadingConfiguration cfg)
    {
        super(DraftV4PointerCollectorDictionary.get());
        final SyntaxValidator syntaxValidator
            = new SyntaxValidator(ValidationConfiguration.byDefault());
        final RefResolver refResolver = new RefResolver(new SchemaLoader(cfg));
        refSyntax = ProcessorChain.startWith(syntaxValidator.getProcessor())
            .chainWith(refResolver).getProcessor();
    }

    @Override
    public SchemaTree processCurrent(final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        final SchemaHolder input = new SchemaHolder(tree);
        final SchemaTree newTree = refSyntax.process(report, input).getValue();
        return EQUIVALENCE.equivalent(tree, newTree) ? tree
            : modify(tree, newTree);
    }

    @Override
    public String toString()
    {
        return "$ref expander";
    }

    private static SchemaTree modify(final SchemaTree tree,
        final SchemaTree newTree)
    {
        final JsonPointer pwd = tree.getPointer();
        if (pwd.isEmpty())
            return newTree;
        final ObjectNode newNode = newTree.getNode().deepCopy();
        final ObjectNode victim = (ObjectNode) tree.getNode();
        victim.removeAll();
        victim.putAll(newNode);
        victim.remove(ImmutableList.of("$schema", "id"));
        return tree;
    }

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode schema = JsonLoader.fromResource("/main.json");
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final SchemaHolder input = new SchemaHolder(tree);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .preloadSchema(schema)
            .preloadSchema(JsonLoader.fromResource("/sub1.json"))
            .preloadSchema(JsonLoader.fromResource("/sub2.json"))
            .freeze();

        final RefExpander expander = new RefExpander(cfg);
        final ProcessingReport report = new DevNullProcessingReport();
        final SchemaHolder output = expander.process(report, input);
        System.out.println(JacksonUtils.prettyPrint(output.getValue()
            .getBaseNode()));
    }
}
