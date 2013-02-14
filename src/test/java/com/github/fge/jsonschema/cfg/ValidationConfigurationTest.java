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
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.library.SchemaVersion;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.ConfigurationMessages.*;
import static org.testng.Assert.*;

public final class ValidationConfigurationTest
{
    private ValidationConfigurationBuilder cfg;

    @BeforeMethod
    public void initConfiguration()
    {
        cfg = ValidationConfiguration.newConfiguration();
    }

    @Test
    public void cannotPutNullLibrary()
    {
        final String ref = "x://y.z/schema#";
        try {
            cfg.addLibrary(ref, null);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_LIBRARY);
        }
    }

    @Test
    public void cannotOverrideExistingLibrary()
    {
        final String ref = "x://y.z/schema#";
        final Library library = Library.newLibrary().freeze();
        try {
            cfg.addLibrary(ref, library);
            cfg.addLibrary(ref, library);
            fail("No exception thrown!!");
        } catch (ValidationConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(DUP_LIBRARY);
        }
    }

    @Test
    public void defaultLibraryIsDraftV4()
    {
        final ValidationConfiguration defaultConfiguration
            = ValidationConfiguration.byDefault();
        assertSame(defaultConfiguration.getDefaultLibrary(),
            SchemaVersion.DRAFTV4.getLibrary());
    }

    @Test
    public void defaultLibraryIsAccountedFor()
    {
        final String ref = "x://y.z/schema#";
        final Library library = Library.newLibrary().freeze();
        cfg.setDefaultLibrary(ref, library);
        assertSame(cfg.freeze().getDefaultLibrary(), library);
    }
}
