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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.keyword.validator.AbstractKeywordValidator;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Keyword;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.assertMessage;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public final class ValidationProcessorTest
{
    private static final String K1 = "k1";
    private static final ObjectNode RAWSCHEMA;
    private static final ArrayNode RAWINSTANCE;
    private static final AtomicInteger COUNT = new AtomicInteger(0);

    static {
        final JsonNodeFactory factory = JacksonUtils.nodeFactory();

        RAWSCHEMA = factory.objectNode();
        RAWSCHEMA.put("minItems", 2)
            .put("items", factory.objectNode().put(K1, 0));

        RAWINSTANCE = factory.arrayNode();
        RAWINSTANCE.add(1);
    }

    private Processor<FullData, FullData> processor;

    @BeforeMethod
    public void init()
    {
        final Keyword keyword = Keyword.newBuilder(K1)
            .withSyntaxChecker(mock(SyntaxChecker.class))
            .withIdentityDigester(NodeType.ARRAY, NodeType.values())
            .withValidatorClass(K1Validator.class)
            .freeze();
        final Library library = DraftV4Library.get().thaw()
            .addKeyword(keyword).freeze();
        final ValidationConfiguration cfg = ValidationConfiguration.newBuilder()
            .setDefaultLibrary("foo://bar#", library).freeze();
        final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(cfg).freeze();
        processor = factory.getProcessor();
        COUNT.set(0);
    }

    @Test
    public void childrenAreNotExploredByDefaultIfContainerFails()
        throws ProcessingException
    {
        final SchemaTree schema
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), RAWSCHEMA);
        final JsonTree instance = new SimpleJsonTree(RAWINSTANCE);
        final FullData data = new FullData(schema, instance);
        final ProcessingReport report = mock(ProcessingReport.class);
        processor.process(report, data);
        assertEquals(COUNT.get(), 0);
    }

    @Test
    public void childrenAreExploredOnDemandEvenIfContainerFails()
        throws ProcessingException
    {
        final SchemaTree schema
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), RAWSCHEMA);
        final JsonTree instance = new SimpleJsonTree(RAWINSTANCE);
        final FullData data = new FullData(schema, instance, true);
        final ProcessingReport report = mock(ProcessingReport.class);
        processor.process(report, data);
        assertEquals(COUNT.get(), 1);
    }

    @Test(timeOut = 1000)
    public void circularReferencingDuringValidationIsDetected()
        throws IOException, ProcessingException, JsonPointerException
    {
        final JsonNode schemaNode
            = JsonLoader.fromResource("/other/issue102.json");
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonValidator validator = factory.getValidator();
        final MessageBundle bundle
            = MessageBundles.getBundle(JsonSchemaValidationBundle.class);

        try {
            validator.validate(schemaNode,
                JacksonUtils.nodeFactory().nullNode());
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            final URI uri = URI.create("#/oneOf/1");
            final ProcessingMessage message = e.getProcessingMessage();
            final String expectedMessage
                = bundle.printf("err.common.validationLoop", uri, "");
            assertMessage(message)
                .hasMessage(expectedMessage)
                .hasField("alreadyVisited", uri)
                .hasField("instancePointer", JsonPointer.empty().toString())
                .hasField("validationPath",
                    Arrays.asList("#", "#/oneOf/1"));
        }
        assertTrue(true);
    }

    /*
     * Issue #112: what was called a "validation loop" in issue #102 was in fact
     * not really one; it is possible to enter the same subschema using
     * different paths.
     *
     * The real thing which must be checked for is a full schema pointer loop.
     */
    @Test
    public void enteringSamePointerWithDifferentPathsDoesNotThrowException()
        throws IOException, ProcessingException
    {
        final JsonNode node = JsonLoader.fromResource("/other/issue112.json");
        final JsonNode schemaNode = node.get("schema");
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonValidator validator = factory.getValidator();

        final JsonNode instance = node.get("instance");

        assertTrue(validator.validate(schemaNode, instance).isSuccess());
        assertTrue(true);
    }

    public static final class K1Validator
        extends AbstractKeywordValidator
    {
        public K1Validator(final JsonNode digest)
        {
            super(K1);
        }

        @Override
        public void validate(final Processor<FullData, FullData> processor,
            final ProcessingReport report, final MessageBundle bundle,
            final FullData data)
            throws ProcessingException
        {
            COUNT.incrementAndGet();
        }

        @Override
        public String toString()
        {
            return K1;
        }
    }
}
