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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.util.RhinoHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * JSON Schema selector for member values of JSON Object instances
 *
 * <p>Unlike what happens with arrays, for a given member name of an instance,
 * here there can be more than one subschema which the member value must be
 * valid against.</p>
 */
public final class ObjectSchemaSelector
{
    private static final JsonPointer PROPERTIES
        = JsonPointer.of("properties");
    private static final JsonPointer PATTERNPROPERTIES
        = JsonPointer.of("patternProperties");
    private static final JsonPointer ADDITIONALPROPERTIES
        = JsonPointer.of("additionalProperties");

    private final List<String> properties;
    private final List<String> patternProperties;
    private final boolean hasAdditional;

    ObjectSchemaSelector(final JsonNode digest)
    {
        hasAdditional = digest.get("hasAdditional").booleanValue();

        List<String> list;

        list = Lists.newArrayList();
        for (final JsonNode node: digest.get("properties"))
            list.add(node.textValue());
        properties = ImmutableList.copyOf(list);

        list = Lists.newArrayList();
        for (final JsonNode node: digest.get("patternProperties"))
            list.add(node.textValue());
        patternProperties = ImmutableList.copyOf(list);
    }

    Iterable<JsonPointer> selectSchemas(final String memberName)
    {
        final List<JsonPointer> list = Lists.newArrayList();

        if (properties.contains(memberName))
            list.add(PROPERTIES.append(memberName));

        for (final String regex: patternProperties)
            if (RhinoHelper.regMatch(regex, memberName))
                list.add(PATTERNPROPERTIES.append(regex));

        if (!list.isEmpty())
            return ImmutableList.copyOf(list);

        return hasAdditional
            ? ImmutableList.of(ADDITIONALPROPERTIES)
            : Collections.<JsonPointer>emptyList();
    }
}
