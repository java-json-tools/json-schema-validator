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

package org.eel.kitchen.jsonschema.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.JsonLoader;
import org.eel.kitchen.util.NodeType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public abstract class AbstractFormatSpecifierTest
{
    private static final Map<NodeType, JsonNode> ALL_PRIMITIVE_TYPES
        = new EnumMap<NodeType, JsonNode>(NodeType.class);

    static {
        final JsonNodeFactory factory = JsonNodeFactory.instance;

        ALL_PRIMITIVE_TYPES.put(NodeType.ARRAY, factory.arrayNode());
        ALL_PRIMITIVE_TYPES.put(NodeType.BOOLEAN, factory.booleanNode(true));
        ALL_PRIMITIVE_TYPES.put(NodeType.INTEGER, factory.numberNode(1));
        ALL_PRIMITIVE_TYPES.put(NodeType.NULL, factory.nullNode());
        ALL_PRIMITIVE_TYPES.put(NodeType.NUMBER, factory.numberNode(1.1));
        ALL_PRIMITIVE_TYPES.put(NodeType.OBJECT, factory.objectNode());
        ALL_PRIMITIVE_TYPES.put(NodeType.STRING, factory.textNode(""));
    }

    protected static final JsonNodeFactory factory
        = JsonNodeFactory.instance;

    private final Set<JsonNode> primitiveTypes = new HashSet<JsonNode>();

    private final FormatSpecifier specifier;

    private final JsonNode testData;

    AbstractFormatSpecifierTest(final FormatSpecifier specifier,
        final NodeType forType, final String resourceName)
        throws IOException
    {
        this.specifier = specifier;

        final Map<NodeType, JsonNode> map
            = new HashMap<NodeType, JsonNode>(ALL_PRIMITIVE_TYPES);

        map.remove(forType);
        primitiveTypes.addAll(map.values());

        testData = JsonLoader.fromResource("/format/" + resourceName + ".json");
    }

    @DataProvider
    protected Iterator<Object[]> getNonApplicableTypes()
    {
        final Set<Object[]> ret = new HashSet<Object[]>(primitiveTypes.size());

        for (final JsonNode node: primitiveTypes)
            ret.add(new Object[] { node });

        return ret.iterator();
    }

    @Test(
        dataProvider = "getNonApplicableTypes"
    )
    public void testNonApplicableTypes(final JsonNode data)
    {
        final ValidationReport report = new ValidationReport();

        specifier.validate(report, data);

        assertTrue(report.isSuccess());
    }

    @DataProvider
    protected Iterator<Object[]> getData()
    {
        final Set<Object[]> set = new HashSet<Object[]>();

        for (final JsonNode node: testData)
            set.add(new Object[] {
                node.get("data"),
                node.get("valid").booleanValue()
            }
            );

        return set.iterator();
    }

    @Test(
        dataProvider = "getData"
    )
    public void testSpecifier(final JsonNode data, final boolean valid)
    {
        final ValidationReport report = new ValidationReport();
        specifier.checkValue(report, data);

        assertEquals(report.isSuccess(), valid);
    }
}
