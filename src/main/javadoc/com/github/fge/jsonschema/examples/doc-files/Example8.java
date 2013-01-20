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

package com.github.fge.jsonschema.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.metaschema.BuiltinSchemas;
import com.github.fge.jsonschema.metaschema.MetaSchema;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;

import java.io.IOException;
import java.util.UUID;

/**
 * Eighth example: augmenting schemas with custom format attributes
 *
 * <p><a href="doc-files/Example8.java">link to source code</a></p>
 *
 * <p><a href="doc-files/custom-fmt.json">link to schema</a></p>
 *
 * <p>This example adds a custom format attribute named {@code uuid}, which
 * checks whether a string instance is a valid UUID.</p>
 *
 * <p>This kind of customization requires three steps:</p>
 *
 * <ul>
 *     <li>grabbing a
 *     {@link com.github.fge.jsonschema.metaschema.MetaSchema.Builder};</li>
 *     <li>augmenting it as needed;</li>
 *     <li>registering it to the {@link JsonSchemaFactory} (via a {@link
 *     com.github.fge.jsonschema.main.JsonSchemaFactory.Builder}, as for all
 *     customizations).</li>
 * </ul>
 *
 * <p>Here, we choose to augment the draft v3 core schema. We can base our new
 * meta-schema by using {@link MetaSchema#basedOn(BuiltinSchemas)} with
 * {@link BuiltinSchemas#DRAFTV3_CORE} as an argument, add our format attribute
 * to it, build it and add it to our factory, using {@link
 * com.github.fge.jsonschema.main.JsonSchemaFactory.Builder#addMetaSchema
 * (MetaSchema, boolean)}. Note that the
 * second argument is {@code true} so that our new meta-schema is regarded as
 * the default.</p>
 *
 * <p>Note also that the schema has no {@code $schema} defined; as a result, the
 * default meta-schema is used (it is <b>not</b> recommended to omit {@code
 * $schema} in your own schemas).</p>
 *
 * <p>Adding a custom format attribute is done by extending the {@link
 * FormatAttribute} class.</p>
 *
 * <p>Two sample files are given: the first (<a
 * href="doc-files/custom-fmt-good.json">link</a>) is valid, the other (<a
 * href="doc-files/custom-fmt-bad.json">link</a>) isn't (the provided {@code id}
 * for the second array element is invalid).</p>
 */
public final class Example8
    extends ExampleBase
{
    public static void main(final String... args)
        throws IOException
    {
        final JsonNode customSchema = loadResource("/custom-fmt.json");
        final JsonNode good = loadResource("/custom-fmt-good.json");
        final JsonNode bad = loadResource("/custom-fmt-bad.json");

        final MetaSchema metaSchema
            = MetaSchema.basedOn(BuiltinSchemas.DRAFTV3_CORE)
                .addFormatAttribute("uuid", UUIDFormatAttribute.getInstance())
                .build();

        final JsonSchemaFactory factory = JsonSchemaFactory.builder()
            .addMetaSchema(metaSchema, true).build();

        final JsonSchema schema = factory.fromSchema(customSchema);

        ValidationReport report;

        report = schema.validate(good);
        printReport(report);

        report = schema.validate(bad);
        printReport(report);
    }

    private static final class UUIDFormatAttribute
        extends FormatAttribute
    {
        private static final FormatAttribute INSTANCE
            = new UUIDFormatAttribute();

        private UUIDFormatAttribute()
        {
            super(NodeType.STRING);
        }

        public static FormatAttribute getInstance()
        {
            return INSTANCE;
        }

        @Override
        public void checkValue(final String fmt, final ValidationReport report,
            final JsonNode value)
        {
            try {
                UUID.fromString(value.textValue());
            } catch (IllegalArgumentException ignored) {
                final Message.Builder msg = newMsg(fmt).addInfo("value", value)
                    .setMessage("string is not a valid UUID");
                report.addMessage(msg.build());
            }
        }

    }
}
