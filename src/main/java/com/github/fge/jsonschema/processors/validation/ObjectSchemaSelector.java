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

package com.github.fge.jsonschema.processors.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.util.RhinoHelper;
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

    public ObjectSchemaSelector(final JsonNode digest)
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

    public Iterable<JsonPointer> selectSchemas(final String memberName)
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
