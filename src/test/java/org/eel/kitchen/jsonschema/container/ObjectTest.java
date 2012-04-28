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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.container;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonValidator;
import org.eel.kitchen.jsonschema.main.ValidationConfig;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

public final class ObjectTest
{
    private JsonNode testNode;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JsonLoader.fromResource("/container/object.json");
    }

    @Test
    public void testAdditionalProperties()
    {
        testOne("additionalProperties");
    }

    @Test
    public void testAdditionalPropertiesSchema()
    {
        testOne("additionalPropertiesSchema");
    }

    @Test
    public void testDependencies()
    {
        testOne("dependencies");
    }

    @Test
    public void testDependenciesSchema()
    {
        testOne("dependenciesSchema");
    }

    // FIXME: this test fails only on full runs...
    @Test
    public void testRequired()
    {
        testOne("required");
    }

    @Test
    public void testPatternProperties()
    {
        testOne("patternProperties");
    }

    @Test
    public void testProperties()
    {
        testOne("properties");
    }

    private void testOne(final String testName)
    {
        final JsonNode node = testNode.get(testName);
        final JsonNode schema = node.get("schema");
        final JsonNode good = node.get("good");
        final JsonNode bad = node.get("bad");

        final ValidationConfig cfg = new ValidationConfig();
        final JsonValidator validator = new JsonValidator(cfg, schema);

        ValidationReport report = validator.validate(good);

        assertTrue(report.isSuccess());
        assertTrue(report.getMessages().isEmpty());

        report = validator.validate(bad);

        assertFalse(report.isSuccess());
    }
}
