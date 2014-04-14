/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LibraryBuilderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);

    @Test
    public void cannotAddNullKeyword()
    {
        try {
            Library.newBuilder().addKeyword(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullKeyword"));
        }
    }

    @Test
    public void cannotRemoveNullKeyword()
    {
        try {
            Library.newBuilder().removeKeyword(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullName"));
        }
    }

    @Test
    public void cannotAddFormatAttributeWithNullName()
    {
        try {
            Library.newBuilder().addFormatAttribute(null,
                mock(FormatAttribute.class));
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullFormat"));
        }
    }

    @Test
    public void cannotAddNullFormatAttribute()
    {
        try {
            Library.newBuilder().addFormatAttribute("foo", null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("nullAttribute", "foo"));
        }
    }

    @Test
    public void cannotRemoveFormatAttributeWithNullName()
    {
        try {
            Library.newBuilder().removeFormatAttribute(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullFormat"));
        }
    }
}
