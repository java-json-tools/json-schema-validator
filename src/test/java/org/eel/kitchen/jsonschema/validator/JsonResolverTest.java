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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.SchemaContainer;
import org.eel.kitchen.jsonschema.main.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

public final class JsonResolverTest
{
    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private SchemaRegistry registry;
    private JsonResolver resolver;
    private SchemaContainer container;
    private  SchemaNode schemaNode;

    @BeforeMethod
    public void initRegistry()
    {
        final URIManager manager = new URIManager();
        final URI namespace = URI.create("");
        registry = new SchemaRegistry(manager, namespace);
        resolver = new JsonResolver(registry);
    }

    @Test
    public void simpleRefLoopIsDetected()
    {
        final JsonNode schema = factory.objectNode().put("$ref", "#");
        container = registry.register(schema);
        schemaNode = new SchemaNode(container, schema);

        try {
            resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref loop detected: [#]");
        }
    }

    @Test
    public void multipleRefLoopIsDetected()
    {
        JsonNode node;
        final ObjectNode schema = factory.objectNode();

        node = factory.objectNode().put("$ref", "#/b");
        schema.put("a", node);

        node = factory.objectNode().put("$ref", "#/c");
        schema.put("b", node);

        node = factory.objectNode().put("$ref", "#/a");
        schema.put("c", node);

        container = new SchemaContainer(schema);
        schemaNode = new SchemaNode(container, schema.get("a"));

        try {
            resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref loop detected: [#/b, #/c, #/a]");
        }
    }

    @Test
    public void simpleDanglingRefIsDetected()
    {
        final JsonNode schema = factory.objectNode().put("$ref", "#foo");

        container = new SchemaContainer(schema);
        schemaNode = new SchemaNode(container, schema);

        try {
            resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "dangling JSON Reference #foo");
        }
    }

    @Test
    public void multipleDangligRefIsDetected()
    {
        JsonNode node;
        final ObjectNode schema = factory.objectNode();

        node = factory.objectNode().put("$ref", "#/b");
        schema.put("a", node);

        node = factory.objectNode().put("$ref", "#/c");
        schema.put("b", node);

        container = new SchemaContainer(schema);
        schemaNode = new SchemaNode(container, schema.get("a"));

        try {
            resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "dangling JSON Reference #/c");
        }
    }

    @Test
    public void crossContextRefLoopIsDetected()
    {
        JsonNode node;

        final String location1 = "http://foo.bar/helloword";
        final String location2 = "zookeeper://127.0.0.1:9000/acrylic#";

        final String ref1 = location2 + "/x";
        final String ref2 = location1 + "#/a";

        final ObjectNode schema1 = factory.objectNode();
        schema1.put("id", location1);

        node = factory.objectNode().put("$ref", ref1);
        schema1.put("a", node);

        final ObjectNode schema2 = factory.objectNode();
        schema2.put("id", location2);

        node = factory.objectNode().put("$ref", ref2);
        schema2.put("x", node);

        registry.register(schema2);

        container = registry.register(schema1);
        schemaNode = new SchemaNode(container, schema1.get("a"));

        try {
            resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "ref loop detected: ["
                + ref1 + ", " + ref2 + ']');
        }
    }

    @Test
    public void crossContextDanglingRefIsDetected()
    {
        JsonNode node;

        final String location1 = "http://foo.bar/helloword";
        final String location2 = "zookeeper://127.0.0.1:9000/acrylic#";

        final String ref1 = location2 + "/x";
        final String ref2 = location1 + "#/b";

        final ObjectNode schema1 = factory.objectNode();
        schema1.put("id", location1);

        node = factory.objectNode().put("$ref", ref1);
        schema1.put("a", node);

        final ObjectNode schema2 = factory.objectNode();
        schema2.put("id", location2);

        node = factory.objectNode().put("$ref", ref2);
        schema2.put("x", node);

        registry.register(schema2);

        container = registry.register(schema1);
        schemaNode = new SchemaNode(container, schema1.get("a"));

        try {
            resolver.resolve(schemaNode);
        } catch (JsonSchemaException e) {
            assertEquals(e.getMessage(), "dangling JSON Reference " + ref2);
        }
    }
}
