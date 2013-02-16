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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.library.validator.DraftV4ValidatorDictionary;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.KeywordValidationMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class NotKeywordTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final ProcessingMessage MSG = new ProcessingMessage();

    private final KeywordValidator validator;

    private Processor<ValidationData, ProcessingReport> processor;
    private ValidationData data;
    private ProcessingReport report;

    public NotKeywordTest()
        throws IllegalAccessException, InvocationTargetException,
        InstantiationException
    {
        final Constructor<? extends KeywordValidator> constructor
            = DraftV4ValidatorDictionary.get().get("not");
        validator = constructor == null ? null
            : constructor.newInstance(FACTORY.nullNode());
    }

    @BeforeMethod
    public void initEnvironment()
    {
        if (validator == null)
            return;

        final ObjectNode schema = FACTORY.objectNode();
        schema.put("not", FACTORY.objectNode());

        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonTree instance = new SimpleJsonTree(FACTORY.nullNode());
        data = new ValidationData(tree, instance);
        report = mock(ProcessingReport.class);
    }

    @Test
    public void keywordExists()
    {
        assertNotNull(validator, "no support for not??");
    }

    @Test(dependsOnMethods = "keywordExists")
    public void exceptionIsCorrectlyThrown()
    {
        processor = new DummyProcessor(WantedState.EX);

        try {
            validator.validate(processor, report, data);
            fail("No exception thrown??");
        } catch (ProcessingException ignored) {
        }
    }

    @Test(dependsOnMethods = "keywordExists")
    public void successfulSubSchemaLeadsToFailure()
        throws ProcessingException
    {
        processor = new DummyProcessor(WantedState.OK);

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        validator.validate(processor, report, data);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isValidationError("not", NOT_FAIL);
    }

    @Test(dependsOnMethods = "keywordExists")
    public void failingSubSchemaLeadsToSuccess()
        throws ProcessingException
    {
        processor = new DummyProcessor(WantedState.KO);

        validator.validate(processor, report, data);

        verify(report, never()).error(anyMessage());
    }

    protected enum WantedState {
        OK
        {
            @Override
            void doIt(final ProcessingReport report)
                throws ProcessingException
            {
            }
        },
        KO
        {
            @Override
            void doIt(final ProcessingReport report)
                throws ProcessingException
            {
                report.error(MSG);
            }
        },
        EX
        {
            @Override
            void doIt(final ProcessingReport report)
                throws ProcessingException
            {
                throw new ProcessingException();
            }
        };

        abstract void doIt(final ProcessingReport report)
            throws ProcessingException;
    }

    protected static final class DummyProcessor
        implements Processor<ValidationData, ProcessingReport>
    {
        private static final JsonPointer PTR
            = JsonPointer.empty().append("not");

        private final WantedState wanted;

        private DummyProcessor(final WantedState wanted)
        {
            this.wanted = wanted;
        }

        @Override
        public ProcessingReport process(final ProcessingReport report,
            final ValidationData input)
            throws ProcessingException
        {
            assertEquals(input.getSchema().getPointer(), PTR);
            wanted.doIt(report);
            return report;
        }
    }
}
