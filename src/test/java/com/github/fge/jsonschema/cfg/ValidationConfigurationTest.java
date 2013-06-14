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

import com.github.fge.jsonschema.exceptions.unchecked.ValidationConfigurationError;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class ValidationConfigurationTest
{
    private static final MessageBundle BUNDLE
        = MessageBundleFactory.getBundle(JsonSchemaConfigurationBundle.class);

    private ValidationConfigurationBuilder cfg;

    @BeforeMethod
    public void initConfiguration()
    {
        cfg = ValidationConfiguration.newBuilder();
    }

    @Test
    public void cannotPutNullLibrary()
    {
        final String ref = "x://y.z/schema#";
        try {
            cfg.addLibrary(ref, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullLibrary"));
        }
    }

    @Test
    public void cannotOverrideExistingLibrary()
    {
        final String ref = "x://y.z/schema#";
        final Library library = Library.newBuilder().freeze();
        try {
            cfg.addLibrary(ref, library);
            cfg.addLibrary(ref, library);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            assertMessage(e.getProcessingMessage()).hasField("uri", ref)
                .hasMessage(BUNDLE.getMessage("dupLibrary"));
        }
    }

    @Test
    public void defaultVersionCannotBeNull()
    {
        try {
            cfg.setDefaultVersion(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("nullVersion"));
        }
    }

    @Test
    public void cannotPutNullSyntaxMessageBundle()
    {
        try {
            cfg.setSyntaxMessages(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("nullMessageBundle"));
        }
    }

    @Test
    public void cannotPutNullValidationMessageBundle()
    {
        try {
            cfg.setValidationMessages(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("nullMessageBundle"));
        }
    }

    @Test
    public void defaultLibraryIsDraftV4()
    {
        final ValidationConfiguration defaultConfiguration
            = ValidationConfiguration.byDefault();
        assertSame(defaultConfiguration.getDefaultLibrary(),
            DraftV4Library.get());
    }

    @Test
    public void defaultLibraryIsAccountedFor()
    {
        final String ref = "x://y.z/schema#";
        final Library library = Library.newBuilder().freeze();
        cfg.setDefaultLibrary(ref, library);
        assertSame(cfg.freeze().getDefaultLibrary(), library);
    }
}
