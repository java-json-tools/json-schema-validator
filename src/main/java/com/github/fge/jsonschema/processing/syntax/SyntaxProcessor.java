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

package com.github.fge.jsonschema.processing.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.ProcessingCache;
import com.github.fge.jsonschema.util.equivalence.SyntaxCheckingEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

public final class SyntaxProcessor
    implements Processor<ValidationData, ValidationData>
{
    private final Dictionary<SyntaxChecker> dict;

    private final ProcessingCache<JsonSchemaTree, SyntaxReport> cache;

    public SyntaxProcessor(final Dictionary<SyntaxChecker> dict)
    {
        this.dict = dict;
        cache = new ProcessingCache<JsonSchemaTree, SyntaxReport>(
            SyntaxCheckingEquivalence.getInstance(), loader());
    }
    /**
     * Process the input
     *
     * @param report the report to use while processing
     * @param input the input for this processor
     * @return the output
     * @throws ProcessingException processing failed
     */
    @Override
    public ValidationData process(final ProcessingReport report,
        final ValidationData input)
        throws ProcessingException
    {
        final JsonSchemaTree inputSchema = input.getSchema();

        /*
         * First check whether the node we have is actually a JSON object.
         * We don't want to cache syntax validation results for _that_.
         */
        final NodeType type
            = NodeType.getNodeType(inputSchema.getCurrentNode());
        if (type != NodeType.OBJECT) {
            final ProcessingMessage msg = newMsg(inputSchema).msg(NOT_A_SCHEMA)
                .put("found", type);
            report.error(msg);
            return input;
        }

        final JsonSchemaTree tree = inputSchema.copy();
        final JsonPointer pointer = inputSchema.getCurrentPointer();
        tree.setPointer(JsonPointer.empty());

        /*
         * The logic is as follows:
         *
         * - fetch the syntax report for this schema at the root;
         * - if the provided pointer is reported as not being validated, trigger
         *   another validation for this same schema at that pointer.
         */
        SyntaxReport syntaxReport = cache.getUnchecked(tree);
        if (syntaxReport.hasIgnoredPath(pointer)) {
            tree.setPointer(pointer);
            syntaxReport = cache.getUnchecked(tree);
        }
        syntaxReport.injectMessages(report);
        return input;
    }

    private CacheLoader<Equivalence.Wrapper<JsonSchemaTree>, SyntaxReport> loader()
    {
        return new CacheLoader<Equivalence.Wrapper<JsonSchemaTree>, SyntaxReport>()
        {
            @Override
            public SyntaxReport load(final Equivalence.Wrapper<JsonSchemaTree> key)
                throws ProcessingException
            {
                final SyntaxReport report = new SyntaxReport();
                validate(report, key.get());
                return report;
            }
        };
    }

    private void validate(final SyntaxReport report, final JsonSchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = tree.getCurrentNode();

        /*
         * Warn about ignored keywords
         */
        final Set<String> fieldNames = Sets.newHashSet(node.fieldNames());
        final Set<String> ignored = dict.missingEntriesFrom(fieldNames);
        if (!ignored.isEmpty()) {
            final JsonPointer pointer = tree.getCurrentPointer();
            for (final String name: ignored)
                report.addIgnoredPath(pointer.append(name));
            report.warn(newMsg(tree).msg(UNKNOWN_KEYWORDS)
                .put("ignored", ignored));
        }

        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final SyntaxChecker checker: dict.valuesForKeys(fieldNames))
            checker.checkSyntax(pointers, report, tree);

        for (final JsonPointer pointer: pointers) {
            tree.append(pointer);
            validate(report, tree);
            tree.pop();
        }
    }

    private static ProcessingMessage newMsg(final JsonSchemaTree tree)
    {
        return new ProcessingMessage().put("schema", tree)
            .put("domain", "syntax");
    }
}
