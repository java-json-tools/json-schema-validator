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
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public final class SchemaSwitchTest
{
    private JsonNode good, bad;
    private JsonValidator validator;
    private final List<String> messages = new LinkedList<String>();

    @BeforeClass
    public void setup()
        throws IOException, JsonValidationFailureException
    {
        final JsonNode node = JsonLoader.fromResource("/other/schemaswitch"
            + ".json");
        good = node.get("good");
        bad = node.get("bad");
        validator = new JsonValidator(node.get("schema"));

        for (final JsonNode msg: node.get("messages"))
            messages.add(msg.getTextValue());
    }

    @Test
    public void testGood()
        throws JsonValidationFailureException
    {
        final ValidationReport report = validator.validate(good);

        assertTrue(report.isSuccess());
    }

    @Test
    public void testBad()
        throws JsonValidationFailureException
    {
        final ValidationReport report = validator.validate(bad);

        assertFalse(report.isSuccess());
        assertFalse(report.isError());

        assertEquals(report.getMessages(), messages);
    }
}
