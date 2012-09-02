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

package org.eel.kitchen.jsonschema.other;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.ref.SchemaContainer;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class FatalErrorTests
{
    private JsonNode testData;
    private JsonSchemaFactory factory;

    private SchemaContainer container;
    private JsonSchema schema;
    private ValidationReport report;

    @BeforeClass
    public void initData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/other/fatal.json");
        factory = new JsonSchemaFactory.Builder().build();
    }

    @DataProvider
    private Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("schema"),
                node.get("data"),
                node.get("message")
            });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void fataErrorsAreReportedAsSuch(final JsonNode node,
        final JsonNode data, final JsonNode message)
    {
        container = factory.registerSchema(node);
        schema = factory.createSchema(container);

        report = schema.validate(data);

        assertTrue(report.hasFatalError());
        assertEquals(report.asJsonNode().iterator().next().get(0), message);
    }
}
