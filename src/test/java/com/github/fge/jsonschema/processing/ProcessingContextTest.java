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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessingContextTest
{
    /*
     * All thresholds except fatal
     */
    private static final EnumSet<LogThreshold> THRESHOLDS
        = EnumSet.complementOf(EnumSet.of(LogThreshold.FATAL));

    @DataProvider
    public Iterator<Object[]> getLogThresholds()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final LogThreshold threshold: THRESHOLDS)
            list.add(new Object[] { threshold });

        // We don't want the values in the same order repeatedly, so...
        Collections.shuffle(list);

        return list.iterator();
    }

    @Test(dataProvider = "getLogThresholds")
    public void logThresholdIsObeyed(final LogThreshold logThreshold)
        throws ProcessingException
    {
        final ProcessingMessage msg = new ProcessingMessage();
        final int nrInvocations  = THRESHOLDS.size() - logThreshold.ordinal();
        final ProcessingContext<Object> ctx = spy(new TestProcessingContext());

        ctx.setLogThreshold(logThreshold);

        for (final LogThreshold threshold: THRESHOLDS)
            ctx.doLog(threshold, msg);

        verify(ctx, times(nrInvocations)).log(msg);
    }

    @Test(dataProvider = "getLogThresholds")
    public void successIsCorrectlyReported(final LogThreshold threshold)
        throws ProcessingException
    {
        final ProcessingContext<Object> ctx = new TestProcessingContext();
        final ProcessingMessage msg = new ProcessingMessage();

        final boolean expected = threshold.compareTo(LogThreshold.ERROR) < 0;

        ctx.doLog(threshold, msg);

        final boolean actual = ctx.isSuccess();
        final String errmsg = "incorrect status report for level " + threshold;

        assertEquals(actual, expected, errmsg);
    }

    @Test(dataProvider = "getLogThresholds")
    public void levelIsCorrectlySetInMessages(final LogThreshold threshold)
        throws ProcessingException
    {
        final ProcessingContext<Object> ctx = new TestProcessingContext();
        final ProcessingMessage msg = new ProcessingMessage();
        ctx.setLogThreshold(threshold);
        ctx.doLog(threshold, msg);

        final JsonNode node = msg.asJson().path("level");
        assertTrue(node.isTextual());
        assertEquals(node.textValue(), threshold.toString());
    }

    @Test(dataProvider = "getLogThresholds")
    public void exceptionThresholdIsObeyed(final LogThreshold logThreshold)
    {
        final EnumSet<LogThreshold> notThrown
            = EnumSet.noneOf(LogThreshold.class);

        for (final LogThreshold threshold: THRESHOLDS) {
            if (threshold.compareTo(logThreshold) >= 0)
                break;
            notThrown.add(threshold);
        }

        final EnumSet<LogThreshold> thrown = EnumSet.complementOf(notThrown);

        final ProcessingContext<Object> ctx = new TestProcessingContext();
        final ProcessingMessage msg = new ProcessingMessage();

        ctx.setExceptionThreshold(logThreshold);

        for (final LogThreshold safe: notThrown)
            try {
                ctx.doLog(safe, msg);
            } catch (ProcessingException ignored) {
                fail("exception thrown at level " + safe
                    + " whereas exception threshold is " + logThreshold + '!');
            }

        for (final LogThreshold oops: thrown)
            try {
                ctx.doLog(oops, msg);
                fail("exception not thrown at level " + oops
                    + " whereas exception threshold is " + logThreshold + '!');
            } catch (ProcessingException ignored) {
            }
    }

    @Test
    public void fatalAlwaysThrowsAnException()
    {
        final ProcessingMessage msg = new ProcessingMessage();
        final ProcessingContext<Object> ctx = new TestProcessingContext();

        try {
            ctx.fatal(msg);
            fail("No exception thrown!");
        } catch (ProcessingException ignored) {
        }
    }

    private static class TestProcessingContext
        extends ProcessingContext<Object>
    {

        @Override
        public void log(final ProcessingMessage msg)
        {
        }

        @Override
        public ProcessingException buildException(final ProcessingMessage msg)
        {
            return new ProcessingException();
        }

        @Override
        public Object getOutput()
        {
            return null;
        }
    }
}
