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

package org.eel.kitchen.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaException;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.report.ValidationReport;

import java.io.IOException;

import static org.eel.kitchen.jsonschema.main.JsonSchemaFactory.*;

/**
 * Sixth example: URI redirection
 *
 * <p><a href="doc-files/Example6.java">link to source code</a></p>
 *
 * <p>In this example, the same schema file is used as in {@link Example1}. This
 * time, though, it is assumed that the base URI used for addressing this schema
 * is {@code http://my.site/schemas/fstab.json#}. But instead of trying to
 * fetch it from the web directly, we want to use the local copy, which is
 * located under URI {@code
 * resource:/org/eel/kitchen/jsonschema/examples/fstab.json#}.</p>
 *
 * <p>The solution is to use another method from {@link Builder}: {@link
 * Builder#addRedirection(String, String)}. This method can be called for as
 * many schemas as you wish to redirect.</p>
 *
 * <p>The effect is that if you required a schema via URI {@code
 * http://my.site/schemas/fstab.json#}, it will silently transform this URI into
 * {@code resource:/org/eel/kitchen/jsonschema/examples/fstab.json#}
 * internally.</p>
 *
 * <p>Note that URIs must be absolute JSON references (see {@link JsonRef}).</p>
 */
public final class Example6
    extends ExampleBase
{
    private static final String FROM = "http://my.site/schemas/fstab.json#";
    private static final String TO
        = "resource:/org/eel/kitchen/jsonschema/examples/fstab.json#";

    public static void main(final String... args)
        throws IOException, JsonSchemaException
    {
        final JsonNode good = loadResource("/fstab-good.json");
        final JsonNode bad = loadResource("/fstab-bad.json");
        final JsonNode bad2 = loadResource("/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.builder()
            .addRedirection(FROM, TO).build();

        final JsonSchema schema = factory.fromURI(FROM);

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);

        report = schema.validate(bad2);
        printReport(report);
    }
}
