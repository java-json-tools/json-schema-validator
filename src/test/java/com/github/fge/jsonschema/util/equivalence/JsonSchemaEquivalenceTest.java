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

package com.github.fge.jsonschema.util.equivalence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.base.Equivalence;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static org.testng.Assert.*;

public final class JsonSchemaEquivalenceTest
{
    private static final Random RND = new Random(System.currentTimeMillis());

    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    @DataProvider
    public Iterator<Object[]> zeroes()
    {
        // At least two decimals
        final int decimalCount = RND.nextInt(20) + 2;
        final StringBuilder sb = new StringBuilder("0.");

        for (int i = 0; i < decimalCount; i++)
            sb.append('0');

        final JsonNode
            integerZero = FACTORY.numberNode(0),
            decimalZero = FACTORY.numberNode(new BigDecimal("0")),
            zeroDotZero = FACTORY.numberNode(new BigDecimal("0.0")),
            bigZero = FACTORY.numberNode(new BigDecimal(sb.toString()));

        return ImmutableSet.of(
            pair(integerZero, decimalZero),
            pair(decimalZero, zeroDotZero),
            pair(zeroDotZero, bigZero),
            pair(bigZero, integerZero)
        ).iterator();
    }

    @Test(dataProvider = "zeroes")
    public void zeroNumberNodesAreCorrectlyHandled(final JsonNode n1,
        final JsonNode n2)
    {
        // Using a hashset will also test for hash code
        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        set.add(EQUIVALENCE.wrap(n1));

        assertFalse(set.add(EQUIVALENCE.wrap(n2)),
            n1 + " and " + n2 + " should be equivalent");
    }

    @DataProvider
    public Iterator<Object[]> randomIntegerNodes()
    {
        final int basenum = RND.nextInt();
        final int decimalCount = RND.nextInt(20) + 2;
        final String intToString = Integer.toString(basenum);
        final StringBuilder sb = new StringBuilder(intToString).append('.');

        for (int i = 0; i < decimalCount; i++)
            sb.append('0');

        final JsonNode
            integer = FACTORY.numberNode(basenum),
            decimal = FACTORY.numberNode(new BigDecimal(intToString)),
            dotZero = FACTORY.numberNode(new BigDecimal(intToString + ".0")),
            decimals = FACTORY.numberNode(new BigDecimal(sb.toString()));

        return ImmutableSet.of(
            pair(integer, decimal),
            pair(decimal, dotZero),
            pair(dotZero, decimals),
            pair(decimals, integer)
        ).iterator();
    }

    @Test(dataProvider = "randomIntegerNodes")
    public void integerNodesAreCorrectlyHandled(final JsonNode n1,
        final JsonNode n2)
    {
        // Using a hashset will also test for hash code
        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        set.add(EQUIVALENCE.wrap(n1));

        assertFalse(set.add(EQUIVALENCE.wrap(n2)),
            n1 + " and " + n2 + " should be equivalent");
    }

    @DataProvider
    public Iterator<Object[]> decimalSamples()
    {
        final JsonNode
            n1 = FACTORY.numberNode(new BigDecimal("923.2323e3")),
            n2 = FACTORY.numberNode(new BigDecimal("923232.3")),
            n3 = FACTORY.numberNode(new BigDecimal("92323230e-2")),
            n4 = FACTORY.numberNode(new BigDecimal("0.9232323e6")),
            n5 = FACTORY.numberNode(new BigDecimal("923232.3000"));

        return ImmutableSet.of(
            pair(n1, n2),
            pair(n2, n3),
            pair(n3, n4),
            pair(n4, n5),
            pair(n5, n1)
        ).iterator();
    }

    @Test(dataProvider = "decimalSamples")
    public void decimalsAreCorrectlyHandled(final JsonNode n1,
        final JsonNode n2)
    {
        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        set.add(EQUIVALENCE.wrap(n1));

        assertFalse(set.add(EQUIVALENCE.wrap(n2)),
            n1 + " and " + n2 + " should be equivalent");
    }

    private static Object[] pair(final JsonNode n1, final JsonNode n2)
    {
        return new Object[] { n1, n2 };
    }
}
