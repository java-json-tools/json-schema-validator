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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.report.Domain;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.schema.AddressingMode;
import org.eel.kitchen.jsonschema.schema.SchemaBundle;
import org.eel.kitchen.jsonschema.schema.SchemaContainer;
import org.eel.kitchen.jsonschema.schema.SchemaNode;
import org.eel.kitchen.jsonschema.schema.SchemaRegistry;
import org.eel.kitchen.jsonschema.uri.URIManager;
import org.eel.kitchen.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public final class JsonResolverTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private SchemaRegistry registry;
    private JsonResolver resolver;
    private SchemaContainer container;
    private SchemaNode schemaNode;

    @BeforeMethod
    public void initRegistry()
    {
        final URIManager manager = new URIManager();
        final URI namespace = URI.create("");
        registry = new SchemaRegistry(manager, namespace,
            AddressingMode.CANONICAL);
        resolver = new JsonResolver(registry);
    }

    @Test
    public void simpleRefLoopIsDetected()
    {
        final JsonNode schema = FACTORY.objectNode().put("$ref", "#");
        container = registry.register(schema);
        schemaNode = new SchemaNode(container, schema);

        final Message expectedMessage = newMsg().setMessage("ref loop detected")
            .addInfo("path", FACTORY.arrayNode().add("#")).build();

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }

    @Test
    public void multipleRefLoopIsDetected()
    {
        JsonNode node;
        final ObjectNode schema = FACTORY.objectNode();
        final ArrayNode path = FACTORY.arrayNode();

        node = FACTORY.objectNode().put("$ref", "#/b");
        schema.put("a", node);
        path.add("#/b");

        node = FACTORY.objectNode().put("$ref", "#/c");
        schema.put("b", node);
        path.add("#/c");

        node = FACTORY.objectNode().put("$ref", "#/a");
        schema.put("c", node);
        path.add("#/a");

        container = AddressingMode.CANONICAL.forSchema(schema);
        schemaNode = new SchemaNode(container, schema.get("a"));

        final Message expectedMessage = newMsg().setMessage("ref loop detected")
            .addInfo("path", path).build();

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }

    @Test
    public void simpleDanglingRefIsDetected()
    {
        final JsonNode schema = FACTORY.objectNode().put("$ref", "#foo");

        container = AddressingMode.CANONICAL.forSchema(schema);
        schemaNode = new SchemaNode(container, schema);

        final Message expectedMessage = newMsg().addInfo("ref", "#foo")
            .setMessage("dangling JSON Reference").build();

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }

    @Test
    public void multipleDangligRefIsDetected()
    {
        JsonNode node;
        final ObjectNode schema = FACTORY.objectNode();

        node = FACTORY.objectNode().put("$ref", "#/b");
        schema.put("a", node);

        node = FACTORY.objectNode().put("$ref", "#/c");
        schema.put("b", node);

        container = AddressingMode.CANONICAL.forSchema(schema);
        schemaNode = new SchemaNode(container, schema.get("a"));

        final Message expectedMessage = newMsg()
            .setMessage("dangling JSON Reference").addInfo("ref", "#/c")
            .build();

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }

    @Test
    public void crossContextRefLoopIsDetected()
        throws JsonSchemaException
    {
        final SchemaBundle bundle;
        final ArrayNode path = FACTORY.arrayNode();

        JsonNode node;

        final String location1 = "http://foo.bar/helloword";
        final String location2 = "zookeeper://127.0.0.1:9000/acrylic#";

        final String ref1 = location2 + "/x";
        final String ref2 = location1 + "#/a";
        path.add(ref1);
        path.add(ref2);

        final ObjectNode schema1 = FACTORY.objectNode();
        schema1.put("id", location1);

        node = FACTORY.objectNode().put("$ref", ref1);
        schema1.put("a", node);

        bundle = new SchemaBundle();
        bundle.addSchema(location1, schema1);

        final ObjectNode schema2 = FACTORY.objectNode();
        schema2.put("id", location2);

        node = FACTORY.objectNode().put("$ref", ref2);
        schema2.put("x", node);

        bundle.addSchema(location2, schema2);

        registry.addBundle(bundle);

        container = registry.get(URI.create(location1));

        schemaNode = new SchemaNode(container, schema1.get("a"));

        final Message expectedMessage = newMsg().setMessage("ref loop detected")
            .addInfo("path", path).build();

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }

    @Test
    public void crossContextDanglingRefIsDetected()
        throws JsonSchemaException
    {
        JsonNode node;

        final SchemaBundle bundle;

        final String location1 = "http://foo.bar/helloword";
        final String location2 = "zookeeper://127.0.0.1:9000/acrylic#";

        final String ref1 = location2 + "/x";
        final String ref2 = location1 + "#/b";

        final ObjectNode schema1 = FACTORY.objectNode();
        schema1.put("id", location1);

        node = FACTORY.objectNode().put("$ref", ref1);
        schema1.put("a", node);

        bundle = new SchemaBundle();
        bundle.addSchema(location1, schema1);

        final ObjectNode schema2 = FACTORY.objectNode();
        schema2.put("id", location2);

        node = FACTORY.objectNode().put("$ref", ref2);
        schema2.put("x", node);

        bundle.addSchema(location2, schema2);

        registry.addBundle(bundle);

        container = registry.get(URI.create(location1));

        schemaNode = new SchemaNode(container, schema1.get("a"));

        final Message expectedMessage = newMsg().addInfo("ref", ref2)
            .setMessage("dangling JSON Reference").build();

        try {
            resolver.resolve(schemaNode);
            fail("No exception thrown!");
        } catch (JsonSchemaException e) {
            assertEquals(e.getValidationMessage(), expectedMessage);
        }
    }

    private static Message.Builder newMsg()
    {
        return Domain.REF_RESOLVING.newMessage().setFatal(true)
            .setKeyword("$ref");
    }
}
