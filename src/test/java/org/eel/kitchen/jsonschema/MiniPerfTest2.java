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

package org.eel.kitchen.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.ref.SchemaContainer;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.util.Map;

public final class MiniPerfTest2
{
    private MiniPerfTest2()
    {
    }

    public static void main(final String... args)
        throws IOException, JsonSchemaException
    {
        final JsonNode draftv3
            = JsonLoader.fromResource("/schema-draftv3.json");
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> schemas
            = JacksonUtils.nodeToMap(googleAPI.get("schemas"));

        final JsonSchemaFactory factory
            = new JsonSchemaFactory.Builder().build();
        final SchemaContainer container = factory.registerSchema(draftv3);
        final JsonSchema schema = factory.createSchema(container);

        long begin, current;
        begin = System.currentTimeMillis();
        doValidate(schemas, schema, -1);
        current = System.currentTimeMillis();

        System.out.println("Initial validation :" + (current - begin) + " ms");

        begin = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            doValidate(schemas, schema, i);
            if (i % 20 == 0) {
                current = System.currentTimeMillis();
                System.out.println(String.format("Iteration %d (in %d ms)", i,
                    current - begin));
            }
        }

        final long end = System.currentTimeMillis();
        System.out.println("END -- time in ms: " + (end - begin));
        System.exit(0);
    }

    private static void doValidate(final Map<String, JsonNode> schemas,
        final JsonSchema schema, final int i)
    {
        String name;
        JsonNode value;
        ValidationReport report;

        for (final Map.Entry<String, JsonNode> entry: schemas.entrySet()) {
            name = entry.getKey();
            value = entry.getValue();
            report = schema.validate(value);
            if (!report.isSuccess()) {
                System.err.println("ERROR: schema " + name + " did not "
                    + "validate (iteration " + i + ')');
                System.exit(1);
            }
        }
    }
}
