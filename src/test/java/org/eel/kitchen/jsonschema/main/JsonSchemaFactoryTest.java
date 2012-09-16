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
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.ValidationContext;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public final class JsonSchemaFactoryTest
{
    @Test
    public void customFormatAttributeIsEffectivelyRegistered()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode()
            .put("format", "foo");

        final FormatAttribute spy = spy(DummyFormatAttribute.instance);

        final JsonSchemaFactory factory = new JsonSchemaFactory.Builder()
            .registerFormat("foo", spy).build();

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
