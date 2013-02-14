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

import com.github.fge.jsonschema.exceptions.unchecked.FactoryConfigurationError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.ConfigurationMessages.*;
import static org.testng.Assert.*;

public final class JsonSchemaFactoryTest
{
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
        } catch (FactoryConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_LOADING_CFG);
        }
    }

    @Test
    public void cannotInsertNullValidationConfiguration()
    {
        try {
            builder.setValidationConfiguration(null);
            fail("No exception thrown!!");
        } catch (FactoryConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_VALIDATION_CFG);
        }
    }

    @Test
    public void cannotInsertNullReportProvider()
    {
        try {
            builder.setReportProvider(null);
            fail("No exception thrown!!");
        } catch (FactoryConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_REPORT_PROVIDER);
        }
    }
}
