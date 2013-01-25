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

package com.github.fge.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.report.Domain;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.schema.AddressingMode;
import com.github.fge.jsonschema.schema.SchemaContext;
import com.github.fge.jsonschema.schema.SchemaNode;
import com.github.fge.jsonschema.schema.SchemaRegistry;
import com.github.fge.jsonschema.uri.URIManager;
import com.github.fge.jsonschema.util.JsonLoader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class InlineRefLoopsTest
{
    private static final URI EMPTY = URI.create("#");

    private JsonNode testData;

    @BeforeClass
    public void initData()
        throws IOException
    {
        testData = JsonLoader.fromResource("/validator/innerRefs.json");
    }

    @DataProvider
    public Iterator<Object[]> getData()
    {
        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("schema"),
                node.get("path")
            });

        return set.iterator();
    }

    @Test(dataProvider = "getData")
    public void idBasedLoopsAreDetected(final JsonNode schema,
        final JsonNode path)
    {
        final URIManager manager = new URIManager();
        final SchemaRegistry registry = new SchemaRegistry(manager,
            EMPTY, AddressingMode.INLINE);
        final JsonResolver resolver = new JsonResolver(registry);

        final SchemaContext schemaContext = registry.register(schema);
        final Message expectedMessage = buildMessage(path);

        final SchemaNode schemaNode = new SchemaNode(schemaContext,
            schemaContext.getSchema());

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }
    private static Message buildMessage(final JsonNode path)
    {
        return Domain.REF_RESOLVING.newMessage().setKeyword("$ref")
            .setFatal(true).setMessage("ref loop detected")
            .addInfo("path", path).build();
    }
}
