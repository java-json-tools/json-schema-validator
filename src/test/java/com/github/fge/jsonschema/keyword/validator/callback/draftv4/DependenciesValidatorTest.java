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

package com.github.fge.jsonschema.keyword.validator.callback.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;

import static com.github.fge.jsonschema.TestUtils.*;
import static org.mockito.Mockito.*;

public final class DependenciesValidatorTest
    extends DraftV4CallbackValidatorTest
{
    public DependenciesValidatorTest()
    {
        super("dependencies", JsonPointer.of("dependencies", "a"),
            JsonPointer.of("dependencies", "b"));
    }

    @Override
    protected void checkOkOk(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, never()).error(anyMessage());
    }

    @Override
    protected void checkOkKo(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, onlyOnce()).error(same(MSG));
    }

    @Override
    protected void checkKoKo(final ProcessingReport report)
        throws ProcessingException
    {
        verify(report, times(2)).error(same(MSG));
    }

    @Override
    protected JsonNode generateSchema()
    {
        final ObjectNode value = FACTORY.objectNode();
        value.put("a", sub1);
        value.put("b", sub2);

        final ObjectNode ret = FACTORY.objectNode();
        ret.put(keyword, value);
        return ret;
    }

    @Override
    protected JsonNode generateInstance()
    {
        final ObjectNode ret = FACTORY.objectNode();
        ret.put("a", "a");
        ret.put("b", "b");
        return ret;
    }

    @Override
    protected JsonNode generateDigest()
    {
        final ArrayNode schemaDeps = FACTORY.arrayNode();
        schemaDeps.add("a");
        schemaDeps.add("b");

        final ObjectNode ret = FACTORY.objectNode();
        ret.put("propertyDeps", FACTORY.objectNode());
        ret.put("schemaDeps", schemaDeps);
        return ret;
    }
}
