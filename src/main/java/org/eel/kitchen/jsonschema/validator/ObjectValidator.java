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

package org.eel.kitchen.jsonschema.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.ref.JsonPointer;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.RhinoHelper;
import org.eel.kitchen.jsonschema.util.jackson.JacksonUtils;

import java.util.Map;
import java.util.Set;

/**
 * Validator called for object instance children
 *
 * <p>Unlike what happens with arrays, a same child/value instance of an object
 * may have to satisfy more than one schema. For a given property name, the list
 * of schemas is constructed as follows:</p>
 *
 * <ul>
 *     <li>if the property name has an exact match in {@code properties},
 *     the corresponding schema is added to the list;</li>
 *     <li>for all regexes in {@code patternProperties}, if the property name
 *     matches the regex, the corresponding schema is added to the list;</li>
 *     <li>if, at this point, the list is empty, then the contents of
 *     {@code additionalProperties} is added to the list (an empty schema if
 *     {@code additionalProperties} is either {@code true} or nonexistent).</li>
 * </ul>
 *
 */
final class ObjectValidator
    implements JsonValidator
{
    private final JsonNode additionalProperties;
    private final Map<String, JsonNode> properties;
    private final Map<String, JsonNode> patternProperties;

    ObjectValidator(final JsonNode schema)
    {
        JsonNode node;

        node = schema.path("additionalProperties");
        additionalProperties = node.isObject() ? node
            : JacksonUtils.emptyObject();

        node = schema.path("properties");
        properties = JacksonUtils.asMap(node);

        node = schema.path("patternProperties");
        patternProperties = JacksonUtils.asMap(node);
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final JsonPointer pwd = report.getPath();
        final Map<String, JsonNode> map = JacksonUtils.asMap(instance);

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            report.setPath(pwd.append(entry.getKey()));
            if (!validateOne(context, report, entry))
                break;
        }

        report.setPath(pwd);
    }

    private boolean validateOne(final ValidationContext context,
        final ValidationReport report, final Map.Entry<String, JsonNode> entry)
    {
        final String key = entry.getKey();
        final JsonNode value = entry.getValue();
        final Set<JsonNode> subSchemas = getSchemas(key);

        JsonValidator validator;

        for (final JsonNode subSchema: subSchemas) {
            validator = context.newValidator(subSchema);
            validator.validate(context, report, value);
            if (report.hasFatalError())
                return false;
        }

        return true;
    }

    @VisibleForTesting
    Set<JsonNode> getSchemas(final String key)
    {
        final Set<JsonNode> ret = Sets.newHashSet();

        if (properties.containsKey(key))
            ret.add(properties.get(key));

        for (final Map.Entry<String, JsonNode> entry:
            patternProperties.entrySet())
            if (RhinoHelper.regMatch(entry.getKey(), key))
                ret.add(entry.getValue());

        if (ret.isEmpty())
            ret.add(additionalProperties);

        return ImmutableSet.copyOf(ret);
    }
}
