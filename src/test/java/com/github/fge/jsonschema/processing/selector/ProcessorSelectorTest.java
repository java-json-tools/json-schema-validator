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

package com.github.fge.jsonschema.processing.selector;

import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Predicate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.TestUtils.*;
import static org.mockito.Mockito.*;

public final class ProcessorSelectorTest
{
    private Predicate<In> predicate1;
    private Processor<In, Out> processor1;

    private Predicate<In> predicate2;
    private Processor<In, Out> processor2;

    private Processor<In, Out> byDefault;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void init()
    {
        predicate1 = mock(Predicate.class);
        processor1 = mock(Processor.class);

        predicate2 = mock(Predicate.class);
        processor2 = mock(Processor.class);

        byDefault = mock(Processor.class);
    }

    @Test
    public void firstComeFirstServed()
        throws ProcessingException
    {
        when(predicate1.apply(anyInput())).thenReturn(true);
        when(predicate2.apply(anyInput())).thenReturn(true);

        final Processor<In, Out> processor  = new ProcessorSelector<In, Out>()
            .when(predicate1).then(processor1)
            .when(predicate2).then(processor2)
            .otherwise(byDefault).getProcessor();

        processor.process(mock(ProcessingReport.class), mock(In.class));

        verify(processor1, onlyOnce()).process(anyReport(), anyInput());
        verifyZeroInteractions(processor2, byDefault);
    }

    private static In anyInput()
    {
        return any(In.class);
    }

    private static Out anyOutput()
    {
        return any(Out.class);
    }

    private interface In extends MessageProvider
    {
    }

    private interface Out extends MessageProvider
    {
    }
}
