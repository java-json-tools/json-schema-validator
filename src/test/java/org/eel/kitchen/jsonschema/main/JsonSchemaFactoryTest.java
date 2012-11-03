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

package org.eel.kitchen.jsonschema.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eel.kitchen.jsonschema.format.FormatAttribute;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistries;
import org.eel.kitchen.jsonschema.metaschema.KeywordRegistry;
import org.eel.kitchen.jsonschema.ref.JsonRef;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class JsonSchemaFactoryTest
{
    @Test
    public void childrenAreNotValidatedIfContainerIsInvalid()
        throws IOException
    {
        final JsonNode testData
            = JsonLoader.fromResource("/other/invalidContainer.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.defaultFactory();

        final JsonNode node = testData.get("schema");
        final JsonNode data = testData.get("data");
        final JsonNode messages = testData.get("messages");

        final JsonSchema schema = factory.fromSchema(node);

        final ValidationReport report = schema.validate(data);

        assertFalse(report.isSuccess());
        assertEquals(report.asJsonObject(), messages);
    }
    @Test
    public void customFormatAttributeIsEffectivelyRegistered()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode()
            .put("format", "foo");

        final FormatAttribute spy = spy(DummyFormatAttribute.instance);

        final KeywordRegistry registry = KeywordRegistries.draftV3();
        registry.addFormatAttribute("foo", spy);
        final JsonSchemaFactory factory = new JsonSchemaFactory.Builder()
            .addKeywordRegistry(JsonRef.emptyRef(), registry, true).build();

        final JsonNode data = JsonNodeFactory.instance.nullNode();
        final JsonSchema schema = factory.fromSchema(node);

        schema.validate(data);
        verify(spy).checkValue(eq("foo"), any(ValidationContext.class),
            any(ValidationReport.class), eq(data));
    }

    private static class DummyFormatAttribute
        extends FormatAttribute
    {
        private static final FormatAttribute instance
            = new DummyFormatAttribute();

        private DummyFormatAttribute()
        {
            super(NodeType.NULL);
        }

        @Override
        public void checkValue(final String fmt, final ValidationContext ctx,
            final ValidationReport report, final JsonNode value)
        {
        }
    }
}
