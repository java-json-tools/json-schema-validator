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

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public final class ProcessorSelector<IN extends MessageProvider, OUT extends MessageProvider>
{
    final Map<Predicate<IN>, Processor<IN, OUT>> choices;
    private final Processor<IN, OUT> byDefault;

    public ProcessorSelector()
    {
        choices = Maps.newLinkedHashMap();
        byDefault = null;
    }

    private ProcessorSelector(
        final Map<Predicate<IN>, Processor<IN, OUT>> choices,
        final Processor<IN, OUT> byDefault)
    {
        this.choices = Maps.newLinkedHashMap(choices);
        this.byDefault = byDefault;
    }

    ProcessorSelector(final ProcessorSelectorPredicate<IN, OUT> selector)
    {
        this(selector.choices, selector.byDefault);
    }

    public ProcessorSelectorPredicate<IN, OUT> when(
        final Predicate<IN> predicate)
    {
        return new ProcessorSelectorPredicate<IN, OUT>(this, predicate,
            byDefault);
    }

    public ProcessorSelector<IN, OUT> otherwise(
        final Processor<IN, OUT> byDefault)
    {
        return new ProcessorSelector<IN, OUT>(choices, byDefault);
    }

    public Processor<IN, OUT> getProcessor()
    {
        return new Chooser<IN, OUT>(choices, byDefault);
    }

    private static final class Chooser<X extends MessageProvider, Y extends MessageProvider>
        implements Processor<X, Y>
    {
        private final Map<Predicate<X>, Processor<X, Y>> map;
        private final Processor<X, Y> byDefault;

        private Chooser(final Map<Predicate<X>, Processor<X, Y>> map,
            final Processor<X, Y> byDefault)
        {
            this.map = ImmutableMap.copyOf(map);
            this.byDefault = byDefault;
        }

        @Override
        public Y process(final ProcessingReport report, final X input)
            throws ProcessingException
        {
            Predicate<X> predicate;
            Processor<X, Y> processor;
            for (final Map.Entry<Predicate<X>, Processor<X, Y>> entry:
                map.entrySet()) {
                predicate = entry.getKey();
                processor = entry.getValue();
                if (predicate.apply(input))
                    return processor.process(report, input);
            }

            if (byDefault != null)
                return byDefault.process(report, input);

            final ProcessingMessage message = input.newMessage()
                .message("no suitable processor found for input");
            throw new ProcessingException(message);
        }
    }
}
