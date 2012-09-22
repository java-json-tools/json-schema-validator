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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class IdBasedSchemaContainerTest
{
    private JsonNode lookups;
    private IdBasedSchemaContainer container;

    @BeforeClass
    public void initialize()
        throws IOException
    {
        final JsonNode testData
            = JsonLoader.fromResource("/schema/idBased.json");
        container = new IdBasedSchemaContainer(testData.get("schema"));
        lookups = testData.get("lookups");
    }

    @DataProvider
    private Iterator<Object[]> getLookupData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: lookups)
            set.add(new Object[] {
                node.get("pointer").textValue(),
                node.get("id").textValue()
            });

        return set.iterator();
    }

    @Test(dataProvider = "getLookupData")
    public void idLookupWorksCorrectly(final String pointer, final String id)
        throws JsonSchemaException
    {
        final JsonRef ref = JsonRef.fromString(id);
        assertTrue(container.contains(ref));

        final JsonNode subSchema = container.resolve(ref);
        assertEquals(subSchema.get("pointer").textValue(), pointer);
    }
}
