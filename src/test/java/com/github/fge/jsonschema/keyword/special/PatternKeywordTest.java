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

package com.github.fge.jsonschema.keyword.special;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.validator.CommonValidatorDictionary;
import com.github.fge.jsonschema.messages.KeywordValidationMessages;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree2;
import com.github.fge.jsonschema.tree.SimpleJsonTree2;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class PatternKeywordTest
{
    /*
     * A special testing class is needed for all keywords which use the null
     * digester, since we cannot feed them with a digest: its information
     * comes with the validation data itself (the schema to be precise).
     */

    private final Constructor<? extends KeywordValidator> constructor;
    private final JsonNode testData;

    public PatternKeywordTest()
        throws IOException
    {
        constructor = CommonValidatorDictionary.get().get("pattern");
        testData = JsonLoader.fromResource("/keyword/special/pattern.json");
    }

    @Test
    public void keywordExists()
    {
        assertNotNull(constructor, "no support for pattern??");
    }

    @DataProvider
    public Iterator<Object[]> getValueTests()
    {
        final List<Object[]> list = Lists.newArrayList();

        KeywordValidationMessages msg;
        JsonNode msgNode;

        for (final JsonNode node: testData) {
            msgNode = node.get("message");
            msg = msgNode == null ? null
                : KeywordValidationMessages.valueOf(msgNode.textValue());
            list.add(new Object[]{ node.get("schema"), node.get("data"), msg,
                node.get("valid").booleanValue(), node.get("msgData") });
        }
        return list.iterator();
    }

    @Test(dataProvider = "getValueTests", dependsOnMethods = "keywordExists")
    public void instancesAreValidatedCorrectly(final JsonNode schema,
        final JsonNode node, final KeywordValidationMessages msg,
        final boolean valid, final ObjectNode msgData)
        throws IllegalAccessException, InvocationTargetException,
        InstantiationException, ProcessingException
    {
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonTree2 instance = new SimpleJsonTree2(node);
        final ValidationData data = new ValidationData(tree, instance);

        final ProcessingReport report = mock(ProcessingReport.class);
        @SuppressWarnings("unchecked")
        final Processor<ValidationData, ProcessingReport> processor
            =  mock(Processor.class);

        // It is a null node which is ignored by the constructor, so we can
        // do that
        final KeywordValidator validator = constructor.newInstance(schema);
        validator.validate(processor, report, data);

        if (valid) {
            verify(report, never()).error(anyMessage());
            return;
        }

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isValidationError("pattern", msg)
            .hasContents(msgData);
    }
}
