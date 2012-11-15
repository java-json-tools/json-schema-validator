/*
 * Copyright (c) 2012, Francis Galiegue <fgaliegue@gmail.com>
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

package org.eel.kitchen.jsonschema.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.*;

public final class CustomJsonNodeFactoryTest
{
    private JsonNode custom;

    @BeforeClass
    public void init()
        throws IOException
    {
        final String resourceName = "/util/nodeFactoryTest.json";

        final ObjectMapper mapper  = new ObjectMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .setNodeFactory(CustomJsonNodeFactory.getInstance());
        final InputStream in = getClass().getResourceAsStream(resourceName);
        custom = mapper.readTree(in);
    }

    @Test
    public void ZeroFilledFractionalPartOrNoFractionalPartIsTheSame()
    {
        final JsonNode n0 = custom.get("one");
        final JsonNode n1 = custom.get("oneDotZero");

        assertEquals(n0, n1, "\"1\" and \"1.0\" are not considered equivalent"
            + " but they should");
    }

    @Test
    public void readerIsUnfazedByZeroOrZeroDotZero()
    {
        final JsonNode n0 = custom.get("zero");
        final JsonNode n1 = custom.get("zeroDotZero");

        assertEquals(n0, n1, "\"0\" and \"0.0\" are not considered equivalent"
            + " but they should");
    }

    @Test(dependsOnMethods = "readerIsUnfazedByZeroOrZeroDotZero")
    public void ZeroIsCastToIntegerZero()
    {
        final JsonNode customZero = custom.get("zero");
        final JsonNode standardZero = JsonNodeFactory.instance.numberNode(0);

        assertEquals(customZero, standardZero, "Custom JsonNodeFactory and "
            + "standard factory don't produce the same JsonNode for 0");
    }

    @Test
    public void integerPathologicalValuesAreHandledCorrectly()
    {
        final JsonNode intMin = custom.get("intMin");
        final JsonNode intMinMinus1 = custom.get("intMinMinus1");
        final JsonNode intMax = custom.get("intMax");
        final JsonNode intMaxPlus1 = custom.get("intMaxPlus1");

        assertSame(intMin.getClass(), IntNode.class,
            "Integer.MIN_VALUE not read as an integer");
        assertSame(intMinMinus1.getClass(), LongNode.class,
            "Integer.MIN_VALUE minus 1 not read as a long");

        assertSame(intMax.getClass(), IntNode.class,
            "Integer.MAX_VALUE not read as an integer");
        assertSame(intMaxPlus1.getClass(), LongNode.class,
            "Integer.MAX_VALUE plus 1 not read as a long");
    }

    @Test
    public void longPathologicalValuesAreHandledCorrectly()
    {
        final JsonNode longMin = custom.get("longMin");
        final JsonNode longMinMinus1 = custom.get("longMinMinus1");
        final JsonNode longMax = custom.get("longMax");
        final JsonNode longMaxPlus1 = custom.get("longMaxPlus1");

        assertSame(longMin.getClass(), LongNode.class,
            "Long.MIN_VALUE not read as an long");
        assertSame(longMinMinus1.getClass(), BigIntegerNode.class,
            "Long.MIN_VALUE minus 1 not read as a BigInteger");

        assertSame(longMax.getClass(), LongNode.class,
            "Long.MAX_VALUE not read as an long");
        assertSame(longMaxPlus1.getClass(), BigIntegerNode.class,
            "Long.MAX_VALUE plus 1 not read as a BigInteger");
    }
}
