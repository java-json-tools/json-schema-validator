/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.processors.validation;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.core.load.RefResolver;
import com.github.fge.jsonschema.core.processing.CachingProcessor;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.processing.ProcessorChain;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.processors.build.ValidatorBuilder;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.digest.SchemaDigester;
import com.github.fge.jsonschema.processors.format.FormatProcessor;
import com.google.common.base.Equivalence;

import javax.annotation.ParametersAreNonnullByDefault;

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
        final ProcessorChain<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>
            chain1
            = ProcessorChain.startWith(refResolver).chainWith(syntaxProcessor);

        resolver = new CachingProcessor<ValueHolder<SchemaTree>, ValueHolder<SchemaTree>>(
            chain1.getProcessor(), SchemaHolderEquivalence.INSTANCE, cfg.getCacheSize()
        );

        final SchemaDigester digester = new SchemaDigester(library);
        final ValidatorBuilder keywordBuilder = new ValidatorBuilder(library);

        ProcessorChain<SchemaContext, ValidatorList> chain2
            = ProcessorChain.startWith(digester).chainWith(keywordBuilder);

        if (cfg.getUseFormat()) {
            final FormatProcessor format = new FormatProcessor(library, cfg);
            chain2 = chain2.chainWith(format);
        }

        builder = new CachingProcessor<SchemaContext, ValidatorList>(
            chain2.getProcessor(), SchemaContextEquivalence.getInstance(), cfg.getCacheSize()
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

    @ParametersAreNonnullByDefault
    private static final class SchemaHolderEquivalence
        extends Equivalence<ValueHolder<SchemaTree>>
    {
        private static final Equivalence<ValueHolder<SchemaTree>> INSTANCE
            = new SchemaHolderEquivalence();

        @Override
        protected boolean doEquivalent(final ValueHolder<SchemaTree> a,
            final ValueHolder<SchemaTree> b)
        {
            return a.getValue().equals(b.getValue());
        }

        @Override
        protected int doHash(final ValueHolder<SchemaTree> t)
        {
            return t.getValue().hashCode();
        }
    }
}
