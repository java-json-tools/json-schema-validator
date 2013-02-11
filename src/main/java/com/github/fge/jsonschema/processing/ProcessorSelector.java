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
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Predicate;
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
        return buildChooser();
    }

    private Processor<IN, OUT> buildChooser()
    {
        return new Processor<IN, OUT>()
        {
            @Override
            public OUT process(final ProcessingReport report, final IN input)
                throws ProcessingException
            {
                for (final Predicate<IN> predicate: choices.keySet())
                    if (predicate.apply(input))
                        return choices.get(predicate).process(report, input);

                if (byDefault == null)
                    throw new ProcessingException("no suitable processor found");

                return byDefault.process(report, input);
            }
        };
    }
}
