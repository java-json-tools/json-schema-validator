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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;

/**
 * A processor chain
 *
 * <p>This class allows to build a chain out of different {@link Processor}
 * instances. The result behaves like a processor itself, so it can be used in
 * other chains as well.</p>
 *
 * <p>Sample usage:</p>
 *
 * <pre>
 *     final Processor&lt;X, Y&gt; chain = ProcessorChain.startWith(p1)
 *         .chainWith(p2).chainWith(...).end();
 *
 *     // input is of type X
 *     final Y ret = chain.process(report, X);
 * </pre>
 *
 * @param <IN> the input type for that chain
 * @param <OUT> the output type for that chain
 */
public final class ProcessorChain<IN extends MessageProvider, OUT extends MessageProvider>
{
    /**
     * The resulting processor
     */
    private final Processor<IN, OUT> processor;

    /**
     * Start a processing chain with a single processor
     *
     * @param p the processor
     * @param <X> the input type
     * @param <Y> the output type
     * @return a single element processing chain
     */
    public static <X extends MessageProvider, Y extends MessageProvider>
        ProcessorChain<X, Y> startWith(final Processor<X, Y> p)
    {
        return new ProcessorChain<X, Y>(p);
    }

    /**
     * Private constructor
     *
     * @param processor the processor
     */
    private ProcessorChain(final Processor<IN, OUT> processor)
    {
        this.processor = processor;
    }

    public ProcessorChain<IN, OUT> failOnError()
    {
        final Processor<OUT, OUT> fail = new Processor<OUT, OUT>()
        {
            @Override
            public OUT process(final ProcessingReport report, final OUT input)
                throws ProcessingException
            {
                if (!report.isSuccess())
                    throw new ProcessingException("chain stopped");
                return input;
            }
        };

        return new ProcessorChain<IN, OUT>(merge(processor, fail));
    }

    /**
     * Add an existing processor to that chain
     *
     * <p>Note that this returns a <b>new</b> chain.</p>
     *
     * @param p2 the processor to add
     * @param <NEWOUT> the return type for that new processor
     * @return a new chain consisting of the previous chain with the new
     * processor appended
     */
    public <NEWOUT extends MessageProvider> ProcessorChain<IN, NEWOUT>
        chainWith(final Processor<OUT, NEWOUT> p2)
    {
        return new ProcessorChain<IN, NEWOUT>(merge(processor, p2));
    }

    public Processor<IN, OUT> end()
    {
        return processor;
    }

    /**
     * Merge two processors
     *
     * @param p1 the first processor
     * @param p2 the second processor
     * @param <X> the input type of {@code p1}
     * @param <Y> the output type of {@code p1} and input type of {@code p2}
     * @param <Z> the output type of {@code p2}
     * @return a processor resulting of applying {@code p2} to the output of
     * {@code p1}
     */
    private static <X extends MessageProvider, Y extends MessageProvider, Z extends MessageProvider>
        Processor<X, Z> merge(final Processor<X, Y> p1, final Processor<Y, Z> p2)
    {
        return new Processor<X, Z>()
        {
            @Override
            public Z process(final ProcessingReport report, final X input)
                throws ProcessingException
            {
                return p2.process(report, p1.process(report, input));
            }
        };
    }
}

