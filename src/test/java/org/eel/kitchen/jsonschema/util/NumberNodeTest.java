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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.SampleNodeProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.testng.Assert.*;

public final class NumberNodeTest
{
    private static final JsonNodeFactory FACTORY
        = CustomJsonNodeFactory.getInstance();

    @DataProvider
    public Iterator<Object[]> invalidNodeTypes()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.INTEGER,
            NodeType.NUMBER);
    }

    @Test(
        dataProvider = "invalidNodeTypes",
        expectedExceptions = IllegalArgumentException.class
    )
    public void invalidNodeTypesAreRecognizedAsSuch(final JsonNode node)
    {
        new NumberNode(node);
    }

    @Test
    public void integerOrDecimalZeroAreEqual()
    {
        final JsonNode integerZero = FACTORY.numberNode(0);
        final JsonNode decimalZero = FACTORY.numberNode(new BigDecimal("0.0"));

        final int hashCode = integerZero.hashCode();

        // Reflexivity
        assertTrue(integerZero.equals(integerZero));

        // Symmetry
        assertTrue(integerZero.equals(decimalZero));
        assertTrue(decimalZero.equals(integerZero));


        // Null test
        assertFalse(integerZero.equals(null));

        // hash code
        assertEquals(decimalZero.hashCode(), hashCode);
    }

    @Test
    public void integerOrDecimalOneAreEqual()
    {
        final JsonNode integerOne = FACTORY.numberNode(1);
        final JsonNode decimalOne = FACTORY.numberNode(new BigDecimal("1.0"));

        final int hashCode = integerOne.hashCode();

        // Reflexivity
        assertTrue(integerOne.equals(integerOne));

        // Symmetry
        assertTrue(integerOne.equals(decimalOne));
        assertTrue(decimalOne.equals(integerOne));


        // Null test
        assertFalse(integerOne.equals(null));

        // hash code
        assertEquals(decimalOne.hashCode(), hashCode);
    }

    @Test(dependsOnMethods = "integerOrDecimalOneAreEqual")
    public void equalsIsTransitive()
    {
        final JsonNode one = FACTORY.numberNode(1);
        final JsonNode oneDotZero = FACTORY.numberNode(new BigDecimal("1.0"));
        final JsonNode oneDotZeroZero
            = FACTORY.numberNode(new BigDecimal("1.00"));

        final int hashCode = one.hashCode();

        assertTrue(oneDotZero.equals(oneDotZeroZero));
        assertTrue(one.equals(oneDotZeroZero));

        assertEquals(oneDotZeroZero.hashCode(), hashCode);
    }
}
