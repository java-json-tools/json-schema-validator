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
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.schema.AbstractJsonValidator;
import org.eel.kitchen.jsonschema.schema.JsonValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.JsonLoader;

import java.io.IOException;
import java.util.Map;

public final class MiniPerfTest2
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode draftv3
            = JsonLoader.fromResource("/schema-draftv3.json");
        final JsonNode googleAPI
            = JsonLoader.fromResource("/other/google-json-api.json");
        final Map<String, JsonNode> schemas
            = CollectionUtils.toMap(googleAPI.get("schemas").fields());

        final JsonValidator validator = AbstractJsonValidator.fromNode(draftv3);

        String name;
        JsonNode value;
        ValidationContext context;

        final long begin = System.currentTimeMillis();

        long current;

        for (int i = 0; i < 500; i++) {
            for (final Map.Entry<String, JsonNode> entry : schemas.entrySet()) {
                context = new ValidationContext();
                name = entry.getKey();
                value = entry.getValue();
                validator.validate(context, value);
                if (!context.isSuccess()) {
                    System.err.println("ERROR: schema " + name + " did not "
                        + "validate (iteration " + i + ")");
                    System.exit(1);
                }
            }
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
}
