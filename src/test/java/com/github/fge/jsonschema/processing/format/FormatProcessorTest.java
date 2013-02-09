/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.processing.format;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.build.FullValidationContext;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.FormatMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class FormatProcessorTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final JsonTree TREE = new SimpleJsonTree(FACTORY.nullNode());
    private static final String FMT = "fmt";

    private FormatAttribute attribute;
    private FormatProcessor processor;
    private ProcessingReport report;

    @BeforeMethod
    public void init()
    {
        attribute = mock(FormatAttribute.class);
        report = mock(ProcessingReport.class);
        final Dictionary<FormatAttribute> dictionary
            = Dictionary.<FormatAttribute>newBuilder().addEntry(FMT, attribute)
                .freeze();
        processor = new FormatProcessor(dictionary);
    }

    @Test
    public void unknownFormatAttributesAreReportedAsWarnings()
        throws ProcessingException
    {
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("format", "foo");
        final JsonSchemaTree schemaTree = new CanonicalSchemaTree(schema);
        final ValidationData data = new ValidationData(schemaTree, TREE);
        final FullValidationContext in = new FullValidationContext(data,
            Collections.<KeywordValidator>emptyList());

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        final FullValidationContext out = processor.process(report, in);

        assertTrue(Lists.newArrayList(out).isEmpty());

        verify(report).warn(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).hasMessage(FORMAT_NOT_SUPPORTED)
            .hasField("domain", "validation").hasField("keyword", "format")
            .hasField("attribute", "foo");
    }
}
