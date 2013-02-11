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
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.Map;

public final class ProcessorSelectorPredicate<IN extends MessageProvider, OUT extends MessageProvider>
{
    private final Predicate<IN> predicate;
    final Map<Predicate<IN>, Processor<IN, OUT>> choices;
    final Processor<IN, OUT> byDefault;

    ProcessorSelectorPredicate(final ProcessorSelector<IN, OUT> selector,
        final Predicate<IN> predicate, final Processor<IN, OUT> byDefault)
    {
        this.predicate = predicate;
        choices = Maps.newLinkedHashMap(selector.choices);
        this.byDefault = byDefault;
    }

    public ProcessorSelector<IN, OUT> then(final Processor<IN, OUT> processor)
    {
        choices.put(predicate, processor);
        return new ProcessorSelector<IN, OUT>(this);
    }
}
