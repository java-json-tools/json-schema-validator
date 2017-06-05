package org.eel.kitchen.jsonschema.tutorial;

import java.io.IOException;

import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * A simple example of using Json schema
 * @author Mateusz Jaracz
 *
 */
public class SimpleTest {

	@Test
	public void testSimpleSchema() throws IOException {
		final JsonNode schemaNode = JsonLoader
				.fromResource("/tutorial/tutorial.json");

		final JsonNode dataNode = JsonLoader
				.fromResource("/tutorial/card.json");
		final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();
		JsonSchema schema = factory.fromSchema(schemaNode);
		ValidationReport report = schema.validate(dataNode);
		boolean success = report.isSuccess();
		Assert.assertTrue(success);
		Assert.assertTrue(report.getMessages().size() == 0);
	}
}
