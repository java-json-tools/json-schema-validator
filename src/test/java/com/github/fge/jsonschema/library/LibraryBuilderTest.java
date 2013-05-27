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
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.messages.ValidationBundles;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LibraryBuilderTest
{
    private static final MessageBundle BUNDLE
        = ValidationBundles.VALIDATION_CFG;

    @Test
    public void cannotAddNullKeyword()
    {
        try {
            Library.newBuilder().addKeyword(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullKeyword"));
        }
    }

    @Test
    public void cannotRemoveNullKeyword()
    {
        try {
            Library.newBuilder().removeKeyword(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullName"));
        }
    }

    @Test
    public void cannotAddFormatAttributeWithNullName()
    {
        try {
            Library.newBuilder().addFormatAttribute(null,
                mock(FormatAttribute.class));
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullFormat"));
        }
    }

    @Test
    public void cannotAddNullFormatAttribute()
    {
        try {
            Library.newBuilder().addFormatAttribute("foo", null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message)
                .hasMessage(BUNDLE.getString("nullAttribute"));
        }
    }

    @Test
    public void cannotRemoveFormatAttributeWithNullName()
    {
        try {
            Library.newBuilder().removeFormatAttribute(null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullFormat"));
        }
    }
}
