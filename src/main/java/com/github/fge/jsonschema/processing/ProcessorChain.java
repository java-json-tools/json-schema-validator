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

public final class ProcessorChain<IN extends MessageProvider, OUT extends MessageProvider>
    implements Processor<IN, OUT>
{
    private final Processor<IN, OUT> p;

    public static <X extends MessageProvider, Y extends MessageProvider>
        ProcessorChain<X, Y> startWith(final Processor<X, Y> p)
    {
        return new ProcessorChain<X, Y>(p);
    }

    private ProcessorChain(final Processor<IN, OUT> p)
    {
        this.p = p;
    }

    public <NEWOUT extends MessageProvider> ProcessorChain<IN, NEWOUT>
        chainWith(final Processor<OUT, NEWOUT> p2)
    {
        return new ProcessorChain<IN, NEWOUT>(merge(p, p2));
    }

    @Override
    public OUT process(final ProcessingReport report, final IN input)
        throws ProcessingException
    {
        return p.process(report, input);
    }

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
