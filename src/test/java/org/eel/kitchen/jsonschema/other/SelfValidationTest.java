/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.other;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.JsonValidator;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.SortedMap;

import static org.testng.Assert.*;

public final class SelfValidationTest
{
    private static final String SPEC = "http://json-schema.org/draft-03/schema";
    private JsonNode draftv3;
    private JsonNode googleAPI;
    private JsonValidator validator;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        draftv3 = JsonLoader.fromURL(new URL(SPEC));
        googleAPI = JsonLoader.fromResource("/other/google-json-api.json");
        validator = new JsonValidator(draftv3);
    }

    @Test
    public void testSchemaValidatesItself()
    {
        final ValidationReport report = validator.validate(draftv3);

        assertTrue(report.isSuccess());
    }

    @Test
    public void testGoogleSchemas()
    {
        final SortedMap<String, JsonNode> schemas
            = CollectionUtils.toSortedMap(googleAPI.get("schemas").getFields());

        ValidationReport report;
        String name;
        JsonNode schema;

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet()) {
            name = entry.getKey();
            schema = entry.getValue();
            report = validator.validate(schema);
            assertTrue(report.isSuccess(), "validation failed for schema "
                + name);
        }
    }

}
