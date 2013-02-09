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

package com.github.fge.jsonschema.processing.digest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.keyword.digest.Digester;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.processing.ValidationDigest;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.tree.JsonTree;
import com.github.fge.jsonschema.tree.SimpleJsonTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.NodeType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaDigesterTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final String K1 = "k1";
    private static final String K2 = "k2";
    private static final EnumSet<NodeType> TYPES1
        = EnumSet.of(NodeType.ARRAY, NodeType.STRING);
    private static final EnumSet<NodeType> TYPES2
        = EnumSet.of(NodeType.BOOLEAN, NodeType.NUMBER, NodeType.OBJECT);

    private final ObjectNode digest1 = FACTORY.objectNode();
    private final ObjectNode digest2 = FACTORY.objectNode();

    private final ObjectNode schema;

    private Digester digester1;
    private Digester digester2;
    private SchemaDigester schemaDigester;

    public SchemaDigesterTest()
    {
        schema = FACTORY.objectNode();
        schema.put(K1, K1);
        schema.put(K2, K2);
    }

    @BeforeMethod
    public void setupDigesters()
    {
        final DictionaryBuilder<Digester> builder = Dictionary.newBuilder();

        digester1 = mock(Digester.class);
        when(digester1.digest(any(JsonNode.class))).thenReturn(digest1);
        when(digester1.supportedTypes()).thenReturn(TYPES1);
        builder.addEntry(K1, digester1);

        digester2 = mock(Digester.class);
        when(digester2.digest(any(JsonNode.class))).thenReturn(digest2);
        when(digester2.supportedTypes()).thenReturn(TYPES2);
        builder.addEntry(K2, digester2);

        schemaDigester = new SchemaDigester(builder.freeze());
    }

    @DataProvider
    public Iterator<Object[]> sampleData()
    {
        return SampleNodeProvider.getSamples(EnumSet.allOf(NodeType.class));
    }

    @Test(dataProvider = "sampleData")
    public void onlyRelevantDigestsAreBuilt(final JsonNode node)
        throws ProcessingException
    {
        final NodeType type = NodeType.getNodeType(node);
        final JsonTree instance = new SimpleJsonTree(node);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final ValidationData data = new ValidationData(tree, instance);
        final ProcessingReport report = mock(ProcessingReport.class);

        final ValidationDigest digest  = schemaDigester.process(report, data);
        verify(digester1).digest(schema);
        verify(digester2).digest(schema);

        final Map<String,JsonNode> digests = digest.getDigests();

        if (TYPES1.contains(type))
            assertSame(digests.get(K1), digest1);
        else
            assertFalse(digests.containsKey(K1));


        if (TYPES2.contains(type))
            assertSame(digests.get(K2), digest2);
        else
            assertFalse(digests.containsKey(K2));

    }

    @Test
    public void nonPresentKeywordDoesNotTriggerBuild()
        throws ProcessingException
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put(K1, K1);
        final JsonSchemaTree schemaTree = new CanonicalSchemaTree(node);
        final JsonNode instance = FACTORY.nullNode();
        final JsonTree tree = new SimpleJsonTree(instance);
        final ValidationData data = new ValidationData(schemaTree, tree);
        final ProcessingReport report = mock(ProcessingReport.class);

        schemaDigester.process(report, data);

        verify(digester1).digest(node);
        verify(digester2, never()).digest(any(JsonNode.class));
    }
}
