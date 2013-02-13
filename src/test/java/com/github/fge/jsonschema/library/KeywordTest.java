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

package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.ValidationConfigurationMessages.*;
import static org.testng.Assert.*;

public final class KeywordTest
{
    @Test
    public void cannotCreateKeywordWithNullName()
    {
        try {
            Keyword.newBuilder(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_NAME);
        }
    }

    @Test
    public void cannotInjectNullSyntaxChecker()
    {
        try {
            Keyword.newBuilder("foo").withSyntaxChecker(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_SYNTAX_CHECKER);
        }
    }

    @Test
    public void cannotInjectNullDigester()
    {
        try {
            Keyword.newBuilder("foo").withDigester(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_DIGESTER);
        }
    }
}
