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
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.LogLevel;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

public final class SyntaxProcessor
    implements Processor<ValidationData, ValidationData>
{
    private final Dictionary<SyntaxChecker> dict;
    private final LoadingCache<JsonNode, ValidatedPaths> cache;

    public SyntaxProcessor(final Dictionary<SyntaxChecker> dict)
    {
        this.dict = dict;
        cache = CacheBuilder.newBuilder().recordStats()
            .build(new CacheLoader<JsonNode, ValidatedPaths>()
            {
                @Override
                public ValidatedPaths load(final JsonNode key)
                    throws Exception
                {
                    return new ValidatedPaths();
                }
            });
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
        final SchemaTree schema = input.getSchema();
        final ListProcessingReport syntaxReport
            = new ListProcessingReport(report);

        syntaxReport.setExceptionThreshold(LogLevel.FATAL);

        final ValidatedPaths paths = cache.getUnchecked(schema.getBaseNode());
        final ReentrantLock lock = paths.getLock();
        lock.lock();
        try {
            if (!paths.isValidated(schema.getPointer())) {
                validate(syntaxReport, schema, paths);
                paths.addReport(syntaxReport);
                if (paths.isValid())
                    paths.addValidatedPath(schema.getPointer());
            }
        } finally {
            lock.unlock();
        }

        for (final ProcessingMessage message: syntaxReport.getMessages())
            report.log(message);

        return input.withSchema(schema.withValidationStatus(paths.isValid()));
    }

    private void validate(final ProcessingReport report,
        final SchemaTree tree, final ValidatedPaths paths)
        throws ProcessingException
    {
        /*
         * Only called if the schema has _not_ been validated yet
         */
        final JsonNode node = tree.getNode();
        final NodeType type = NodeType.getNodeType(node);

        if (type != NodeType.OBJECT) {
            final ProcessingMessage msg = newMsg(tree).msg(NOT_A_SCHEMA)
                .put("found", type);
            report.error(msg);
            return;
        }

        /*
         * Warn about ignored keywords
         */
        final Set<String> fieldNames = Sets.newHashSet(node.fieldNames());
        final Set<String> ignored = dict.missingEntriesFrom(fieldNames);
        if (!ignored.isEmpty()) {
            final JsonPointer pointer = tree.getPointer();
            for (final String name: ignored)
                paths.addUncheckedPath(pointer.append(name));
            report.warn(newMsg(tree).msg(UNKNOWN_KEYWORDS)
                .put("ignored", ignored));
        }

        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final SyntaxChecker checker: dict.valuesForKeys(fieldNames))
            checker.checkSyntax(pointers, report, tree);

        for (final JsonPointer pointer: pointers)
            validate(report, tree.append(pointer), paths);
    }

    private static ProcessingMessage newMsg(final SchemaTree tree)
    {
        return new ProcessingMessage().put("schema", tree)
            .put("domain", "syntax");
    }

    @Override
    public String toString()
    {
        return "Syntax checker";
    }
}
