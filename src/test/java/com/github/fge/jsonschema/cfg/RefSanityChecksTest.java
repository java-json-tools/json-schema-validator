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

package com.github.fge.jsonschema.cfg;

import com.github.fge.jsonschema.exceptions.unchecked.JsonReferenceError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.JsonReferenceErrors.*;
import static org.testng.Assert.*;

public final class RefSanityChecksTest
{
    @Test
    public void nullInputRaisesException()
    {
        try {
            RefSanityChecks.absoluteRef(null);
            fail("No exception thrown!!");
        } catch (JsonReferenceError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_URI);
        }
    }

    @Test
    public void invalidURIRaisesException()
    {
        final String input = "+24:x/t/";
        try {
            RefSanityChecks.absoluteRef(input);
            fail("No exception thrown!!");
        } catch (JsonReferenceError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(INVALID_URI)
                .hasField("input", input);
        }
    }

    @Test
    public void nonAbsoluteJsonReferenceRaisesException()
    {
        final String input = "foo://bar/baz#/a";
        try {
            RefSanityChecks.absoluteRef(input);
            fail("No exception thrown!!");
        } catch (JsonReferenceError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(REF_NOT_ABSOLUTE)
                .hasField("input", input);
        }
    }
}
