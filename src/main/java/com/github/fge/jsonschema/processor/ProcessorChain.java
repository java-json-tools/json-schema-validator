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

package com.github.fge.jsonschema.processor;

import java.math.BigInteger;

public final class ProcessorChain<IN, OUT>
    implements Processor<IN, OUT>
{
    private final Processor<IN, OUT> p;

    public static <X, Y> ProcessorChain<X, Y> startWith(final Processor<X, Y> p)
    {
        return new ProcessorChain<X, Y>(p);
    }

    private ProcessorChain(final Processor<IN, OUT> p)
    {
        this.p = p;
    }

    public <NEWOUT> ProcessorChain<IN, NEWOUT> chainWith(
        final Processor<OUT, NEWOUT> p2)
    {
        return new ProcessorChain<IN, NEWOUT>(merge(p, p2));
    }

    @Override
    public OUT process(final IN input)
    {
        return p.process(input);
    }

    private static <X, Y, Z> Processor<X, Z> merge(
        final Processor<X, Y> p1, final Processor<Y, Z> p2)
    {
        return new Processor<X, Z>()
        {
            @Override
            public Z process(final X input)
            {
                return p2.process(p1.process(input));
            }
        };
    }

    public static void main(final String... args)
    {
        final Processor<String, Integer> p1 = new ProcessorImpl();
        final Processor<Integer, BigInteger> p2 = new ProcessorImpl2();

        final ProcessorChain<String, BigInteger> chain
            = ProcessorChain.startWith(p1).chainWith(p2);

        System.out.println(chain.process("32"));
    }
}
