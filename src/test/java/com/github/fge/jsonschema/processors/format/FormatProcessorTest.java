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

package com.github.fge.jsonschema.processors.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.JsonTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.format.FormatAttribute;
import com.github.fge.jsonschema.keyword.validator.KeywordValidator;
import com.github.fge.jsonschema.messages.JsonSchemaValidationBundle;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class FormatProcessorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaValidationBundle.class);
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String FMT = "fmt";
    private static final EnumSet<NodeType> SUPPORTED
        = EnumSet.of(NodeType.INTEGER, NodeType.NUMBER, NodeType.BOOLEAN);

    private FormatAttribute attribute;
    private FormatProcessor processor;
    private ProcessingReport report;

    @BeforeMethod
    public void init()
    {
        attribute = mock(FormatAttribute.class);
        when(attribute.supportedTypes()).thenReturn(SUPPORTED);
        report = mock(ProcessingReport.class);
        final Dictionary<FormatAttribute> dictionary
            = Dictionary.<FormatAttribute>newBuilder().addEntry(FMT, attribute)
                .freeze();
        processor = new FormatProcessor(dictionary);
    }

    @Test
    public void noFormatInSchemaIsANoOp()
        throws ProcessingException
    {
        final ObjectNode schema = FACTORY.objectNode();
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
        final SchemaContext context = new SchemaContext(tree, NodeType.NULL);
        final ValidatorList in = new ValidatorList(context,
            Collections.<KeywordValidator>emptyList());

        final ValidatorList out = processor.process(report, in);

        assertTrue(Lists.newArrayList(out).isEmpty());

        verifyZeroInteractions(report);
    }

    @Test
    public void unknownFormatAttributesAreReportedAsWarnings()
        throws ProcessingException
    {
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("format", "foo");
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
        final SchemaContext context = new SchemaContext(tree, NodeType.NULL);
        final ValidatorList in = new ValidatorList(context,
            Collections.<KeywordValidator>emptyList());

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        final ValidatorList out = processor.process(report, in);

        assertTrue(Lists.newArrayList(out).isEmpty());

        verify(report).warn(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message)
            .hasMessage(BUNDLE.printf("warn.format.notSupported", "foo"))
            .hasField("domain", "validation").hasField("keyword", "format")
            .hasField("attribute", "foo");
    }

    @Test
    public void attributeIsBeingAskedWhatIsSupports()
        throws ProcessingException
    {
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("format", FMT);
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
        final SchemaContext context = new SchemaContext(tree, NodeType.NULL);
        final ValidatorList in = new ValidatorList(context,
            Collections.<KeywordValidator>emptyList());

        processor.process(report, in);
        verify(attribute).supportedTypes();
    }

    @DataProvider
    public Iterator<Object[]> supported()
    {
        return SampleNodeProvider.getSamples(SUPPORTED);
    }

    @Test(
        dataProvider = "supported",
        dependsOnMethods = "attributeIsBeingAskedWhatIsSupports"
    )
    public void supportedNodeTypesTriggerAttributeBuild(final JsonNode node)
        throws ProcessingException
    {
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("format", FMT);
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
        final JsonTree instance = new SimpleJsonTree(node);
        final FullData data = new FullData(tree, instance);
        final SchemaContext context = new SchemaContext(data);
        final ValidatorList in = new ValidatorList(context,
            Collections.<KeywordValidator>emptyList());

        final ValidatorList out = processor.process(report, in);

        final List<KeywordValidator> validators = Lists.newArrayList(out);

        assertEquals(validators.size(), 1);

        @SuppressWarnings("unchecked")
        final Processor<FullData, FullData> p = mock(Processor.class);

        validators.get(0).validate(p, report, BUNDLE, data);
        verify(attribute).validate(report, BUNDLE, data);
    }

    @DataProvider
    public Iterator<Object[]> unsupported()
    {
        return SampleNodeProvider.getSamplesExcept(SUPPORTED);
    }

    @Test(
        dataProvider = "unsupported",
        dependsOnMethods = "attributeIsBeingAskedWhatIsSupports"
    )
    public void unsupportedTypeDoesNotTriggerValidatorBuild(final JsonNode node)
        throws ProcessingException
    {
        final ObjectNode schema = FACTORY.objectNode();
        schema.put("format", FMT);
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
        final SchemaContext context
            = new SchemaContext(tree, NodeType.getNodeType(node));
        final ValidatorList in = new ValidatorList(context,
            Collections.<KeywordValidator>emptyList());

        final ValidatorList out = processor.process(report, in);

        final List<KeywordValidator> validators = Lists.newArrayList(out);

        assertTrue(validators.isEmpty());
    }
}
