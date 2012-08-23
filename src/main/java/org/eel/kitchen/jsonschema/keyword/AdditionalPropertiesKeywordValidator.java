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

package org.eel.kitchen.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.util.RhinoHelper;
import org.eel.kitchen.jsonschema.validator.ObjectJsonValidator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for {@code additionalProperties}
 *
 * <p>Note that this keyword only handles validation at the instance level: it
 * does not validate children.</p>
 *
 * <p>The rules are:</p>
 * <ul>
 *     <li>if {@code additionalProperties} is a schema or {@code true},
 *     validation succeeds;</li>
 *     <li>if it is {@code false}, then validation succeeds if and only if
 *     all instance members are either members in {@code properties} or match
 *     at least one regex of {@code patternProperties}.</li>
 *     </li>
 * </ul>
 *
 * @see ObjectJsonValidator
 */
public final class AdditionalPropertiesKeywordValidator
    extends KeywordValidator
{
    private final boolean additionalOK;
    private final Set<String> properties;
    private final Set<String> patternProperties;

    public AdditionalPropertiesKeywordValidator(final JsonNode schema)
    {
        super("additionalProperties", NodeType.OBJECT);
        additionalOK = schema.get(keyword).asBoolean(true);

        if (additionalOK) {
            properties = Collections.emptySet();
            patternProperties = Collections.emptySet();
            return;
        }

        ImmutableSet.Builder<String> builder;

        builder = new ImmutableSet.Builder<String>();
        if (schema.has("properties"))
            builder.addAll(schema.get("properties").fieldNames());
        properties = builder.build();

        builder = new ImmutableSet.Builder<String>();
        if (schema.has("patternProperties"))
            builder.addAll(schema.get("patternProperties").fieldNames());
        patternProperties = builder.build();
    }

    @Override
    public void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (additionalOK)
            return;

        final Set<String> fields = JacksonUtils.fieldNames(instance);

        fields.removeAll(properties);

        final Set<String> tmp = new HashSet<String>();

        for (final String field: fields)
            for (final String regex: patternProperties)
                if (RhinoHelper.regMatch(regex, field))
                    tmp.add(field);

        fields.removeAll(tmp);

        if (!fields.isEmpty())
            report.addMessage("additional properties not permitted");
    }
}
