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

package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.keyword.FullValidationContext;
import com.github.fge.jsonschema.processing.keyword.KeywordBuilder;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.processing.keyword.KeywordDescriptorBuilder;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.Iterator;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class KeywordBuilderTest
{
    private static final String KEYWORD = "k";
    private static final EnumSet<NodeType> TYPES
        = EnumSet.of(NodeType.STRING, NodeType.BOOLEAN, NodeType.ARRAY);

    private Processor<ValidationData, FullValidationContext> processor;
    private ProcessingReport report;

    @BeforeMethod
    public void init()
    {
        final KeywordDescriptorBuilder b = KeywordDescriptor.newBuilder();
        b.setValidatorClass(TestKeyword.class);
        b.setValidatedTypes(NodeType.STRING, NodeType.BOOLEAN, NodeType.ARRAY);
        final DictionaryBuilder<KeywordDescriptor> builder
            = Dictionary.newBuilder();
        final Dictionary<KeywordDescriptor> dict = builder
                .addEntry(KEYWORD, b.freeze()).freeze();
        processor = new KeywordBuilder(dict);
        report = mock(ProcessingReport.class);
    }

    @DataProvider
    public Iterator<Object[]> validatingTypes()
    {
        return SampleNodeProvider.getSamples(TYPES);
    }

    @Test(dataProvider = "validatingTypes")
    public void validatingTypesTriggerValidatorBuild(final JsonNode node)
        throws ProcessingException
    {
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put(KEYWORD, KEYWORD);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final JsonTree instance = new SimpleJsonTree(node);
        final ValidationData data = new ValidationData(tree, instance);

        final FullValidationContext set = processor.process(report, data);
        assertFalse(Lists.newArrayList(set).isEmpty());
    }

    @DataProvider
    public Iterator<Object[]> nonValidatingTypes()
    {
        return SampleNodeProvider.getSamplesExcept(TYPES);
    }

    @Test(dataProvider = "nonValidatingTypes")
    public void ignoredTypesDoNotTriggerValidatorBuild(final JsonNode node)
    {

    }

    /*
     * Public since it is built by reflection...
     */
    public static class TestKeyword
        implements KeywordValidator
    {
        public TestKeyword(final JsonNode ignored)
        {
        }

        @Override
        public void validate(
            final Processor<ValidationData, ProcessingReport> processor,
            final ProcessingReport report, final ValidationData data)
            throws ProcessingException
        {
        }
    }
}
