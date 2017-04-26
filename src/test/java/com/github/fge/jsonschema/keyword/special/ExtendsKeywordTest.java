/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.keyword.special;

import static com.github.fge.jsonschema.TestUtils.anyMessage;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.assertMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.KeywordValidatorFactory;
import com.github.fge.jsonschema.library.validator.DraftV3ValidatorDictionary;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

public final class ExtendsKeywordTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaValidationBundle.class);
    private static final String FOO = "foo";
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final KeywordValidator validator;

    private Processor<FullData, FullData> processor;
    private FullData data;
    private ProcessingReport report;
    private ProcessingMessage msg;

    public ExtendsKeywordTest()
        throws ProcessingException
    {
        final KeywordValidatorFactory factory
            = DraftV3ValidatorDictionary.get().entries().get("extends");
        validator = factory == null ? null
            : factory.getKeywordValidator(FACTORY.nullNode());
    }

    @BeforeMethod
    public void initEnvironment()
    {
        if (validator == null)
            return;

        final ObjectNode schema = FACTORY.objectNode();
        schema.put("extends", FACTORY.objectNode());
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);

        final JsonTree instance = new SimpleJsonTree(FACTORY.nullNode());
        data = new FullData(tree, instance);

        report = mock(ProcessingReport.class);
        msg = new ProcessingMessage().setMessage(FOO);
    }

    @Test
    public void keywordExists()
    {
        assertNotNull(validator, "no support for extends??");
    }

    @Test(dependsOnMethods = "keywordExists")
    public void exceptionIsCorrectlyThrown()
    {
        processor = new DummyProcessor(WantedState.EX, msg);

        try {
            validator.validate(processor, report, BUNDLE, data);
            fail("No exception thrown??");
        } catch (ProcessingException ignored) {
        }
    }

    @Test(dependsOnMethods = "keywordExists")
    public void failingSubSchemaLeadsToFailure()
        throws ProcessingException
    {
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        processor = new DummyProcessor(WantedState.KO, msg);

        validator.validate(processor, report, BUNDLE, data);

        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).hasMessage(FOO);
    }

    @Test(dependsOnMethods = "keywordExists")
    public void successfulSubSchemaLeadsToSuccess()
        throws ProcessingException
    {
        processor = new DummyProcessor(WantedState.OK, msg);

        validator.validate(processor, report, BUNDLE, data);

        verify(report, never()).error(anyMessage());
    }

    private enum WantedState {
        OK
        {
            @Override
            void doIt(final ProcessingReport report,
                final ProcessingMessage message)
                throws ProcessingException
            {
            }
        },
        KO
        {
            @Override
            void doIt(final ProcessingReport report,
                final ProcessingMessage message)
                throws ProcessingException
            {
                report.error(message);
            }
        },
        EX
        {
            @Override
            void doIt(final ProcessingReport report,
                final ProcessingMessage message)
                throws ProcessingException
            {
                throw new ProcessingException();
            }
        };

        abstract void doIt(final ProcessingReport report,
            final ProcessingMessage message)
            throws ProcessingException;
    }

    private static final class DummyProcessor
        implements Processor<FullData, FullData>
    {
        private static final JsonPointer PTR = JsonPointer.of("extends");

        private final WantedState wanted;
        private final ProcessingMessage message;

        private DummyProcessor(final WantedState wanted,
            final ProcessingMessage message)
        {
            this.wanted = wanted;
            this.message = message;
        }

        @Override
        public FullData process(final ProcessingReport report,
            final FullData input)
            throws ProcessingException
        {
            assertEquals(input.getSchema().getPointer(), PTR);
            wanted.doIt(report, message);
            return input;
        }
    }
}
