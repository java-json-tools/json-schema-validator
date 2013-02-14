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
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessorChainTest
{
    @Test
    public void failingOnErrorExitsEarly()
        throws ProcessingException
    {
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p1
            = mock(Processor.class);
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p2
            = mock(Processor.class);

        final Processor<MessageProvider, MessageProvider> processor
            = ProcessorChain.startWith(p1).failOnError().chainWith(p2).getProcessor();

        final MessageProvider input = mock(MessageProvider.class);
        final ProcessingReport report = mock(ProcessingReport.class);
        when(report.isSuccess()).thenReturn(false);

        try {
            processor.process(report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException ignored) {
        }

        verify(p1).process(same(report), any(MessageProvider.class));
        verify(p2, never()).process(any(ProcessingReport.class),
            any(MessageProvider.class));
    }

    @Test
    public void noFailureDoesNotTriggerEarlyExit()
        throws ProcessingException
    {
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p1
            = mock(Processor.class);
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p2
            = mock(Processor.class);

        final Processor<MessageProvider, MessageProvider> processor
            = ProcessorChain.startWith(p1).failOnError().chainWith(p2).getProcessor();

        final MessageProvider input = mock(MessageProvider.class);
        final ProcessingReport report = mock(ProcessingReport.class);
        when(report.isSuccess()).thenReturn(true);

        processor.process(report, input);

        verify(p1).process(same(report), any(MessageProvider.class));
        verify(p2).process(same(report), any(MessageProvider.class));
    }
}
