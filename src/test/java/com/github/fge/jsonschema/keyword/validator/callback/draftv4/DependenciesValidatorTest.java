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

package com.github.fge.jsonschema.keyword.validator.callback.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

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
