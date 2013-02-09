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

package com.github.fge.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.messages.FormatMessages;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public abstract class AbstractFormatAttributeTest
{
    protected static final JsonSchemaTree SCHEMA_TREE
        = new CanonicalSchemaTree(JacksonUtils.emptyObject());

    protected final FormatAttribute attribute;
    protected final JsonNode testNode;
    protected final String fmt;

    protected ProcessingReport report;

    protected AbstractFormatAttributeTest(
        final Dictionary<FormatAttribute> dict, final String prefix,
        final String fmt)
        throws IOException
    {
        final String resourceName = String.format("/format/%s/%s.json",
            prefix, fmt);
        this.fmt = fmt;
        testNode = JsonLoader.fromResource(resourceName);
        attribute = dict.get(fmt);
    }

    @BeforeMethod
    public final void initReport()
    {
        report = mock(ProcessingReport.class);
    }

    @Test
    public final void formatAttributeIsSupported()
    {
        assertNotNull(attribute, "no support for format attribute " + fmt);
    }

    @DataProvider
    public final Iterator<Object[]> testData()
    {
        final List<Object[]> list = Lists.newArrayList();

        FormatMessages msg;
        JsonNode msgNode;

        for (final JsonNode node: testNode) {
            msgNode = node.get("message");
            msg = msgNode == null ? null
                : FormatMessages.valueOf(msgNode.textValue());
            list.add(new Object[]{
                node.get("data"),
                node.get("valid").booleanValue(),
                msg,
                node.get("msgData")
            });
        }

        return list.iterator();
    }

    @Test(
        dataProvider = "testData",
        dependsOnMethods = "formatAttributeIsSupported"
    )
    public final void instanceIsCorrectlyAnalyzed(final JsonNode instance,
        final boolean valid, final FormatMessages msg, final ObjectNode msgData)
        throws ProcessingException
    {
        final JsonTree tree = new SimpleJsonTree(instance);
        final ValidationData data = new ValidationData(SCHEMA_TREE, tree);

        attribute.validate(report, data);

        if (valid) {
            verifyZeroInteractions(report);
            return;
        }

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isFormatMessage(fmt, msg).hasContents(msgData)
            .hasField("value", instance);
    }
}
