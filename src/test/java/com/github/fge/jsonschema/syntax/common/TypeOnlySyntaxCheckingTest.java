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

package com.github.fge.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.processing.ValidationData;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.CommonSyntaxCheckingTest;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.mockito.Mockito.*;

public abstract class TypeOnlySyntaxCheckingTest
    extends CommonSyntaxCheckingTest
{
    protected TypeOnlySyntaxCheckingTest(final String keyword,
        final NodeType first, final NodeType... other)
        throws IOException
    {
        super(keyword, first, other);
    }

    @DataProvider
    public final Iterator<Object[]> validTypes()
    {
        return SampleNodeProvider.getSamplesExcept(invalidTypes);
    }


    @Test(dataProvider = "validTypes")
    public final void validTypesValidateSuccessfully(final JsonNode node)
        throws ProcessingException
    {
        final ProcessingReport report = mock(ProcessingReport.class);
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put(keyword, node);
        final JsonSchemaTree tree = new CanonicalSchemaTree(schema);
        final ValidationData data = new ValidationData(tree);

        processor.process(report, data);

        verify(report, never()).log(any(ProcessingMessage.class));
    }
}
