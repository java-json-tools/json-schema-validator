/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class JsonSchemaFactoryTest
{
    @Test
    public void childrenAreNotValidatedIfContainerIsInvalid()
        throws IOException
    {
        final JsonNode testData
            = JsonLoader.fromResource("/other/invalidContainer.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();

        final JsonNode node = testData.get("schema");
        final JsonNode data = testData.get("data");
        final JsonNode messages = testData.get("messages");

        final JsonSchema schema = factory.fromSchema(node);

        final ValidationReport report = schema.validate(data);

        assertFalse(report.isSuccess());
        assertEquals(report.asJsonObject(), messages);
    }
}
