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
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessorSelectorTest
{
    private Predicate<In> predicate1;
    private Processor<In, Out> processor1;

    private Predicate<In> predicate2;
    private Processor<In, Out> processor2;

    private Processor<In, Out> byDefault;

    private ProcessorSelector<In, Out> selector;
    private List<Processor<In, Out>> otherProcessors;

    private In input;
    private ProcessingReport report;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void init()
    {
        /*
         * We want to ensure order: have two "real" mocks, and 10 dummy others.
         */
        predicate1 = mock(Predicate.class);
        processor1 = mock(Processor.class);

        predicate2 = mock(Predicate.class);
        processor2 = mock(Processor.class);

        byDefault = mock(Processor.class);

        otherProcessors = Lists.newArrayList();

        selector = new ProcessorSelector<In, Out>();

        Predicate<In> predicate;
        Processor<In, Out> processor;

        for (int i = 0; i < 5; i++) {
            predicate = mock(Predicate.class);
            when(predicate.apply(any(In.class))).thenReturn(false);
            processor = mock(Processor.class);
            otherProcessors.add(processor);
            selector = selector.when(predicate).then(processor);
        }

        selector = selector.when(predicate1).then(processor1);

        for (int i = 0; i < 5; i++) {
            predicate = mock(Predicate.class);
            when(predicate.apply(any(In.class))).thenReturn(false);
            processor = mock(Processor.class);
            otherProcessors.add(processor);
            selector = selector.when(predicate).then(processor);
        }

        selector = selector.when(predicate2).then(processor2);

        input = mock(In.class);
        when(input.newMessage()).thenReturn(new ProcessingMessage());
        report = mock(ProcessingReport.class);
    }

    @Test
    public void firstComeFirstServed()
        throws ProcessingException
    {
        when(predicate1.apply(input)).thenReturn(true);
        when(predicate2.apply(input)).thenReturn(true);

        final Processor<In, Out> processor = selector.otherwise(byDefault)
            .getProcessor();

        processor.process(report, input);

        verify(processor1, onlyOnce()).process(same(report), same(input));
        verifyZeroInteractions(processor2, byDefault);

        for (final Processor<In, Out> p: otherProcessors)
            verifyZeroInteractions(p);
    }

    @Test
    public void firstSuccessfulPredicateIsExecuted()
        throws ProcessingException
    {
        when(predicate1.apply(input)).thenReturn(false);
        when(predicate2.apply(input)).thenReturn(true);

        final Processor<In, Out> processor = selector.otherwise(byDefault)
            .getProcessor();

        processor.process(report, input);

        verify(processor2, onlyOnce()).process(same(report), same(input));
        verifyZeroInteractions(processor1, byDefault);
        for (final Processor<In, Out> p: otherProcessors)
            verifyZeroInteractions(p);
    }

    @Test
    public void noSuccessfulPredicateAndNoDefaultThrowsException()
    {
        when(predicate1.apply(input)).thenReturn(false);
        when(predicate2.apply(input)).thenReturn(false);

        final Processor<In, Out> processor = selector.getProcessor();

        try {
            processor.process(report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            verifyZeroInteractions(processor1, processor2);
            for (final Processor<In, Out> p: otherProcessors)
                verifyZeroInteractions(p);
            assertMessage(e.getProcessingMessage())
                .hasMessage("no suitable processor found for input");
        }
    }

    @Test
    public void noSuccessfulPredicateExecutesDefault()
        throws ProcessingException
    {
        when(predicate1.apply(input)).thenReturn(false);
        when(predicate2.apply(input)).thenReturn(false);

        final Processor<In, Out> processor = selector.otherwise(byDefault)
            .getProcessor();

        processor.process(report, input);

        verifyZeroInteractions(processor1, processor2);
        verify(byDefault, onlyOnce()).process(report, input);

        for (final Processor<In, Out> p: otherProcessors)
            verifyZeroInteractions(p);
    }

    private interface In extends MessageProvider
    {
    }

    private interface Out extends MessageProvider
    {
    }
}
