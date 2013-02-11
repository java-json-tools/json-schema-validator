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

package com.github.fge.jsonschema.keyword.syntax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.main.JsonSchemaException;
import com.github.fge.jsonschema.messages.SyntaxMessages;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree2;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.SyntaxMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public abstract class SyntaxCheckersTest
{
    /*
     * The keyword
     */
    private final String keyword;
    /*
     * The syntax checker
     */
    private final SyntaxChecker checker;
    /*
     * The set of invalid types for that keyword
     */
    private final EnumSet<NodeType> invalidTypes;
    /*
     * The value test node, if any
     */
    private final JsonNode valueTests;
    /*
     * The pointer test node, if any
     */
    private final JsonNode pointerTests;

    /*
     * Per test variables
     */
    private List<JsonPointer> pointers;
    private ProcessingReport report;

    /**
     * Constructor
     *
     * @param dict the {@link Dictionary} of {@link SyntaxChecker}s
     * @param prefix the prefix to use for resource files
     * @param keyword the keyword to test
     * @throws JsonProcessingException source JSON (if any) is not legal JSON
     */
    protected SyntaxCheckersTest(final Dictionary<SyntaxChecker> dict,
        final String prefix, final String keyword)
        throws JsonProcessingException
    {
        this.keyword = keyword;
        checker = dict.get(keyword);
        invalidTypes = checker == null ? null
            : EnumSet.complementOf(checker.getValidTypes());
        /*
         * Try and load the data and affect pointers. Barf on invalid JSON.
         *
         * If IOException, it means no file (hopefully); affect a MissingNode
         * to both valueTests and pointerTests.
         */
        JsonNode valueTestsNode, pointerTestsNode;
        try {
            final String resource = "/keyword/syntax/" + prefix + '/' + keyword
                + ".json";
            final JsonNode data = JsonLoader.fromResource(resource);
            valueTestsNode = data.path("valueTests");
            pointerTestsNode = data.path("pointerTests");
        } catch (JsonProcessingException oops) {
            throw oops;
        } catch (IOException ignored) {
            valueTestsNode = MissingNode.getInstance();
            pointerTestsNode = MissingNode.getInstance();
        }

        valueTests = valueTestsNode;
        pointerTests = pointerTestsNode;
    }

    @BeforeMethod
    public final void init()
    {
        pointers = Lists.newArrayList();
        report = mock(ProcessingReport.class);
    }

    /*
     * First test: check the keyword's presence in the dictionary. All other
     * tests depend on this one.
     */
    @Test
    public final void keywordIsSupportedInThisDictionary()
    {
        assertNotNull(checker, "keyword " + keyword + " is not supported??");
    }

    /*
     * Second test: check that invalid values are reported as such. Test common
     * to all keywords.
     */
    @DataProvider
    public final Iterator<Object[]> invalidTypes()
    {
        return SampleNodeProvider.getSamples(invalidTypes);
    }

    @Test(
        dependsOnMethods = "keywordIsSupportedInThisDictionary",
        dataProvider = "invalidTypes"
    )
    public final void invalidTypesAreReportedAsErrors(final JsonNode node)
        throws ProcessingException
    {
        final SchemaTree tree = treeFromValue(keyword, node);
        final NodeType type = NodeType.getNodeType(node);
        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);

        checker.checkSyntax(pointers, report, tree);

        verify(report).error(captor.capture());

        final ProcessingMessage msg = captor.getValue();
        assertMessage(msg).isSyntaxError(keyword, INCORRECT_TYPE, tree)
            .hasField("expected", EnumSet.complementOf(invalidTypes))
            .hasField("found", type);
    }

    /*
     * Third test: value tests. If no value tests were found, don't bother:
     * AbstractSyntaxCheckerTest has covered that for us.
     */
    @DataProvider
    protected final Iterator<Object[]> getValueTests()
    {
        if (valueTests.isMissingNode())
            return Iterators.emptyIterator();

        final List<Object[]> list = Lists.newArrayList();

        SyntaxMessages message;
        JsonNode msgNode;

        for (final JsonNode node: valueTests) {
            msgNode = node.get("message");
            message = msgNode == null ? null
                : SyntaxMessages.valueOf(msgNode.textValue());
            list.add(new Object[]{ node.get("schema"), message,
                node.get("valid").booleanValue(), node.get("msgData") });
        }
        return list.iterator();
    }

    @Test(
        dependsOnMethods = "keywordIsSupportedInThisDictionary",
        dataProvider = "getValueTests"
    )
    public final void valueTestsSucceed(final JsonNode schema,
        final SyntaxMessages syntaxMessage, final boolean success,
        final ObjectNode msgData)
        throws ProcessingException
    {
        final SchemaTree tree = new CanonicalSchemaTree2(schema);

        checker.checkSyntax(pointers, report, tree);

        if (success) {
            verify(report, never()).error(anyMessage());
            return;
        }

        final ArgumentCaptor<ProcessingMessage> captor
            = ArgumentCaptor.forClass(ProcessingMessage.class);
        verify(report).error(captor.capture());

        final ProcessingMessage message = captor.getValue();

        assertMessage(message).isSyntaxError(keyword, syntaxMessage, tree)
            .hasContents(msgData);
    }

    /*
     * Fourth test: pointer lookups
     *
     * Non relevant keywrods will not have set it
     */
    @DataProvider
    protected final Iterator<Object[]> getPointerTests()
    {
        if (pointerTests.isMissingNode())
            return Iterators.emptyIterator();

        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode node: pointerTests)
            list.add(new Object[] {
                node.get("schema"), node.get("pointers")
            });

        return list.iterator();
    }

    @Test(
        dependsOnMethods = "keywordIsSupportedInThisDictionary",
        dataProvider = "getPointerTests"
    )
    public final void pointerDelegationWorksCorrectly(final JsonNode schema,
        final ArrayNode expectedPointers)
        throws ProcessingException, JsonSchemaException
    {
        final SchemaTree tree = new CanonicalSchemaTree2(schema);

        checker.checkSyntax(pointers, report, tree);

        final List<JsonPointer> expected = Lists.newArrayList();
        for (final JsonNode node: expectedPointers)
            expected.add(new JsonPointer(node.textValue()));

        assertEquals(pointers, expected);
    }

    /*
     * Utility methods
     */
    private static SchemaTree treeFromValue(final String keyword,
        final JsonNode node)
    {
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put(keyword, node);
        return new CanonicalSchemaTree2(schema);
    }
}
