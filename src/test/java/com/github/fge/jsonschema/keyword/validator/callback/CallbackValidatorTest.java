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

package com.github.fge.jsonschema.keyword.validator.callback;

import static com.github.fge.jsonschema.TestUtils.anyReport;
import static com.github.fge.jsonschema.TestUtils.onlyOnce;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.keyword.validator.KeywordValidatorFactory;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

@Test
public abstract class CallbackValidatorTest
{
    protected static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaValidationBundle.class);
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    protected static final ProcessingMessage MSG = new ProcessingMessage();

    protected static final ObjectNode sub1 = FACTORY.objectNode();
    protected static final ObjectNode sub2 = FACTORY.objectNode();

    protected final String keyword;
    private final KeywordValidatorFactory factory;
    protected final JsonPointer ptr1;
    protected final JsonPointer ptr2;

    private Processor<FullData, FullData> processor;
    private FullData data;
    private ProcessingReport report;
    private KeywordValidator validator;

    protected CallbackValidatorTest(
        final Dictionary<KeywordValidatorFactory> dict,
        final String keyword, final JsonPointer ptr1, final JsonPointer ptr2)
    {
        this.keyword = keyword;
        factory = dict.entries().get(keyword);
        this.ptr1 = ptr1;
        this.ptr2 = ptr2;
    }

    @BeforeMethod
    protected final void initEnvironment()
        throws ProcessingException
    {
        if (factory == null)
            return;

        final SchemaTree tree = new CanonicalSchemaTree(
            SchemaKey.anonymousKey(), generateSchema());
        final JsonTree instance = new SimpleJsonTree(generateInstance());
        data = new FullData(tree, instance);
        report = mock(ProcessingReport.class);
        when(report.getLogLevel()).thenReturn(LogLevel.DEBUG);
        validator = factory.getKeywordValidator(generateDigest());
    }

    @Test
    public final void keywordExists()
    {
        assertNotNull(factory, "no support for " + keyword + "??");
    }

    @Test(dependsOnMethods = "keywordExists")
    public final void exceptionOnFirstProcessingWorks()
        throws ProcessingException
    {
        processor = spy(new DummyProcessor(WantedState.EX, WantedState.OK, ptr1,
            ptr2));

        try {
            validator.validate(processor, report, BUNDLE, data);
            fail("No exception thrown!!");
        } catch (ProcessingException ignored) {
        }

        verify(processor, onlyOnce()).process(anyReport(), any(FullData.class));
    }

    @Test(dependsOnMethods = "keywordExists")
    public final void exceptionOnSecondProcessingWorks()
        throws ProcessingException
    {
        processor = spy(new DummyProcessor(WantedState.OK, WantedState.EX, ptr1,
            ptr2));

        try {
            validator.validate(processor, report, BUNDLE, data);
            fail("No exception thrown!!");
        } catch (ProcessingException ignored) {
        }

        verify(processor, times(2)).process(anyReport(), any(FullData.class));
    }

    @Test(dependsOnMethods = "keywordExists")
    public final void OkThenOkWorks()
        throws ProcessingException
    {
        processor = spy(new DummyProcessor(WantedState.OK, WantedState.OK, ptr1,
            ptr2));

        validator.validate(processor, report, BUNDLE, data);
        verify(processor, times(2)).process(anyReport(), any(FullData.class));

        checkOkOk(report);
    }

    protected abstract void checkOkOk(final ProcessingReport report)
        throws ProcessingException;

    @Test(dependsOnMethods = "keywordExists")
    public final void OkThenKoWorks()
        throws ProcessingException
    {
        processor = spy(new DummyProcessor(WantedState.OK, WantedState.KO, ptr1,
            ptr2));

        validator.validate(processor, report, BUNDLE, data);
        verify(processor, times(2)).process(anyReport(), any(FullData.class));

        checkOkKo(report);
    }

    protected abstract void checkOkKo(final ProcessingReport report)
        throws ProcessingException;

    @Test(dependsOnMethods = "keywordExists")
    public final void KoThenKoWorks()
        throws ProcessingException
    {
        processor = spy(new DummyProcessor(WantedState.KO, WantedState.KO, ptr1,
            ptr2));

        validator.validate(processor, report, BUNDLE, data);
        verify(processor, times(2)).process(anyReport(), any(FullData.class));

        checkKoKo(report);
    }

    protected abstract void checkKoKo(final ProcessingReport report)
        throws ProcessingException;

    protected abstract JsonNode generateSchema();

    protected abstract JsonNode generateInstance();

    protected abstract JsonNode generateDigest();

    private enum WantedState {
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

    private static class DummyProcessor
        implements Processor<FullData, FullData>
    {
        private final WantedState wanted1;
        private final WantedState wanted2;
        private final JsonPointer ptr1;
        private final JsonPointer ptr2;

        private DummyProcessor(final WantedState wanted1,
            final WantedState wanted2, final JsonPointer ptr1,
            final JsonPointer ptr2)
        {
            this.wanted1 = wanted1;
            this.wanted2 = wanted2;
            this.ptr1 = ptr1;
            this.ptr2 = ptr2;
        }

        @Override
        public FullData process(final ProcessingReport report,
            final FullData input)
            throws ProcessingException
        {
            final JsonNode schema = input.getSchema().getNode();

            final JsonPointer ptr = schema == sub1 ? ptr1 : ptr2;
            assertEquals(input.getSchema().getPointer(), ptr,
                "schema pointer differs from expectations");

            final WantedState wanted = schema == sub1 ? wanted1 : wanted2;
            wanted.doIt(report);
            return input;
        }
    }
}
