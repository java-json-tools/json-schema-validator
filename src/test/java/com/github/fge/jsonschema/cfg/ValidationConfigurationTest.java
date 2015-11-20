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

package com.github.fge.jsonschema.cfg;

import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.messages.JsonSchemaConfigurationBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class ValidationConfigurationTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaConfigurationBundle.class);

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
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),BUNDLE.printf("dupLibrary", ref));
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
    public void cannotPutInvalidCacheSize()
    {
        try {
            cfg.setCacheSize(-2);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("invalidCacheSize"));
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
