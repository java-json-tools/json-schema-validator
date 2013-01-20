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

package grimbo;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class NonAbsoluteSchemaUris
{
    private JsonSchema schema;

    @BeforeMethod
    public void beforeTest()
        throws JsonSchemaException
    {
        final JsonSchemaFactory factory = JsonSchemaFactory.builder()
            .setNamespace("resource:/grimbo/").build();
        schema = factory.fromURI("child1/child.json");
    }

    @Test
    public void testEmptyObject()
        throws IOException
    {
        final JsonNode data
            = JsonLoader.fromResource("/grimbo/empty-object.json");

        final ValidationReport report = schema.validate(data);

        assertFalse(report.isSuccess());
    }

    @Test
    public void testTestObject()
        throws IOException
    {
        final JsonNode data
            = JsonLoader.fromResource("/grimbo/test-object.json");

        final ValidationReport report = schema.validate(data);

        assertTrue(report.isSuccess());
    }

    @Test
    public void testTestObjectNoBodyItem()
        throws IOException
    {
        final JsonNode data
            = JsonLoader.fromResource("/grimbo/test-object-no-bodyItem.json");

        final ValidationReport report = schema.validate(data);

        assertFalse(report.isSuccess());
    }
}
