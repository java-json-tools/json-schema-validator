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

package com.github.fge.jsonschema.processors.validation;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.jsonschema.core.util.equivalence.SchemaTreeEquivalence;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.load.RefResolver;
import com.github.fge.jsonschema.processing.CachingProcessor;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.base.Equivalence;

/**
 * A validation chain
 *
 * <p>This processor performs the following:</p>
 *
 * <ul>
 *     <li>perform reference lookup then syntax validation;</li>
 *     <li>throw an exception if the previous step fails;</li>
 *     <li>then perform schema digesting and keyword building.</li>
 * </ul>
 *
 * <p>A validation chain handles one schema version. Switching schema versions
 * is done by {@link ValidationProcessor}.</p>
 */
public final class ValidationChain
    implements Processor<SchemaContext, ValidatorList>
{
    private final Processor<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>
        resolver;
    private final Processor<SchemaContext, ValidatorList> builder;

    public ValidationChain(final RefResolver refResolver,
        final Library library, final ValidationConfiguration cfg)
    {
        final SyntaxProcessor syntaxProcessor = new SyntaxProcessor(
            cfg.getSyntaxMessages(), library.getSyntaxCheckers());
        final ProcessorChain<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>> chain1
            = ProcessorChain.startWith(refResolver).chainWith(syntaxProcessor);

        resolver = new CachingProcessor<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>(
            chain1.getProcessor(), SchemaHolderEquivalence.INSTANCE
        );

        final SchemaDigester digester = new SchemaDigester(library);
        final ValidatorBuilder keywordBuilder = new ValidatorBuilder(library);

        ProcessorChain<SchemaContext, ValidatorList> chain2
            = ProcessorChain.startWith(digester).chainWith(keywordBuilder);

        if (cfg.getUseFormat()) {
            final FormatProcessor format
                = new FormatProcessor(library, cfg);
            chain2 = chain2.chainWith(format);
        }

        builder = new CachingProcessor<SchemaContext, ValidatorList>(
            chain2.getProcessor(), SchemaContextEquivalence.getInstance()
        );
    }

    @Override
    public ValidatorList process(final ProcessingReport report,
        final SchemaContext input)
        throws ProcessingException
    {
        final ValueHolder<SchemaTree> in
            = ValueHolder.hold("schema", input.getSchema());

        /*
         * We have to go through an intermediate report. If we re-enter this
         * function with a report already telling there is an error, we don't
         * want to wrongly report that the schema is invalid.
         */
        final ProcessingReport r = new ListProcessingReport(report);
        final ValueHolder<SchemaTree> out = resolver.process(r, in);
        report.mergeWith(r);
        if (!r.isSuccess())
            return null;

        final SchemaContext output = new SchemaContext(out.getValue(),
            input.getInstanceType());
        return builder.process(report, output);
    }

    @Override
    public String toString()
    {
        return resolver + " -> " + builder;
    }

    private static final class SchemaHolderEquivalence
        extends Equivalence<ValueHolder<SchemaTree>>
    {
        private static final Equivalence<ValueHolder<SchemaTree>> INSTANCE
            = new SchemaHolderEquivalence();

        private static final Equivalence<SchemaTree> EQUIVALENCE
            = SchemaTreeEquivalence.getInstance();

        @Override
        protected boolean doEquivalent(final ValueHolder<SchemaTree> a,
            final ValueHolder<SchemaTree> b)
        {
            return EQUIVALENCE.equivalent(a.getValue(), b.getValue());
        }

        @Override
        protected int doHash(final ValueHolder<SchemaTree> t)
        {
            return EQUIVALENCE.hash(t.getValue());
        }
    }
}
