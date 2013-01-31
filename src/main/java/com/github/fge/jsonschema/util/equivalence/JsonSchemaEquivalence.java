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
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An {@link Equivalence} strategy for JSON Schema equality
 *
 * <p>{@link JsonNode} does a pretty good job of obeying the  {@link
 * Object#equals(Object) equals()}/{@link Object#hashCode() hashCode()}
 * contract. And in fact, it does it too well for JSON Schema.</p>
 *
 * <p>For instance, it considers numeric nodes {@code 1} and {@code 1.0} to be
 * different nodes, which is true. But JSON Schema mandates that numeric JSON
 * values are equal if their mathematical value is the same. This class
 * enforces this kind of equality.</p>
 */
// TODO: use better hash functions for arrays and objects
public final class JsonSchemaEquivalence
    extends Equivalence<JsonNode>
{
    private static final Equivalence<JsonNode> INSTANCE
        = new JsonSchemaEquivalence();

    private JsonSchemaEquivalence()
    {
    }

    public static Equivalence<JsonNode> getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected boolean doEquivalent(final JsonNode a, final JsonNode b)
    {
        /*
         * If both are numbers, delegate to the helper method
         */
        if (a.isNumber() && b.isNumber())
            return numEquals(a, b);

        final NodeType typeA = NodeType.getNodeType(a);
        final NodeType typeB = NodeType.getNodeType(b);

        /*
         * If they are of different types, no dice
         */
        if (typeA != typeB)
            return false;

        /*
         * For all other primitive types than numbers, trust JsonNode
         */
        if (!a.isContainerNode())
            return a.equals(b);

        /*
         * OK, so they are containers (either both arrays or objects due to the
         * test on types above). They are obviously not equal if they do not
         * have the same number of elements/members.
         */
        if (a.size() != b.size())
            return false;

        /*
         * Delegate to the appropriate method according to their type.
         */
        return typeA == NodeType.ARRAY ? arrayEquals(a, b) : objectEquals(a, b);
    }

    @Override
    protected int doHash(final JsonNode t)
    {
        /*
         * If this is a numeric node, we want the same hashcode for the same
         * mathematical values. Go with double, its range is good enough for
         * 99+% of use cases.
         */
        if (t.isNumber())
            return Double.valueOf(t.doubleValue()).hashCode();

        /*
         * If this is a primitive type (other than numbers, handled above),
         * delegate to JsonNode.
         */
        if (!t.isContainerNode())
            return t.hashCode();

        /*
         * The following hash calculations work, yes, but they are poor at best.
         * And probably slow, too.
         *
         * TODO: try and figure out those hash classes from Guava
         */
        int ret = 0;

        /*
         * If the container is empty, just return
         */
        if (t.size() == 0)
            return ret;

        /*
         * Array
         */
        if (t.isArray()) {
            for (final JsonNode element : t)
                ret = 31 * ret + doHash(element);
            return ret;
        }

        /*
         * Not an array? An object.
         */
        final Iterator<Map.Entry<String, JsonNode>> iterator = t.fields();

        Map.Entry<String, JsonNode> entry;

        while (iterator.hasNext()) {
            entry = iterator.next();
            ret = 31 * ret
                + (entry.getKey().hashCode() ^ doHash(entry.getValue()));
        }

        return ret;
    }

    private static boolean numEquals(final JsonNode a, final JsonNode b)
    {
        /*
         * If both numbers are integers, delegate to JsonNode.
         */
        if (a.isIntegralNumber() && b.isIntegralNumber())
            return a.equals(b);

        /*
         * Otherwise, compare decimal values.
         */
        return a.decimalValue().compareTo(b.decimalValue()) == 0;
    }

    private boolean arrayEquals(final JsonNode a, final JsonNode b)
    {
        /*
         * We are guaranteed here that arrays are the same size.
         */
        final int size = a.size();

        for (int i = 0; i < size; i++)
            if (!doEquivalent(a.get(i), b.get(i)))
                return false;

        return true;
    }

    private boolean objectEquals(final JsonNode a, final JsonNode b)
    {
        /*
         * Grab the key set from the first node
         */
        final Set<String> keys = Sets.newHashSet(a.fieldNames());

        /*
         * Grab the key set from the second node, and see if both sets are the
         * same. If not, objects are not equal, no need to check for children.
         */
        final Set<String> set = Sets.newHashSet(b.fieldNames());
        if (!set.equals(keys))
            return false;

        /*
         * Test each member individually.
         */
        for (final String key: keys)
            if (!doEquivalent(a.get(key), b.get(key)))
                return false;

        return true;
    }
}
