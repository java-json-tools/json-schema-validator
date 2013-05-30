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

package com.github.fge.jsonschema.main;

import com.github.fge.jsonschema.cfg.ConfigurationMessageBundle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class JsonSchemaFactoryTest
{
    private static final ConfigurationMessageBundle BUNDLE
        = ConfigurationMessageBundle.getInstance();

    private JsonSchemaFactoryBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = JsonSchemaFactory.newBuilder();
    }

    @Test
    public void cannotInsertNullLoadingConfiguration()
    {
        try {
            builder.setLoadingConfiguration(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("nullLoadingCfg"));
        }
    }

    @Test
    public void cannotInsertNullValidationConfiguration()
    {
        try {
            builder.setValidationConfiguration(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("nullValidationCfg"));
        }
    }

    @Test
    public void cannotInsertNullReportProvider()
    {
        try {
            builder.setReportProvider(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("nullReportProvider"));
        }
    }
}
