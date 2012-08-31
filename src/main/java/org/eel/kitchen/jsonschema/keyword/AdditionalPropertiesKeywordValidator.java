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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.util.RhinoHelper;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

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
 */
public final class AdditionalPropertiesKeywordValidator
    extends KeywordValidator
{
    private final Joiner TOSTRING_JOINER = Joiner.on("; or ");
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

        if (fields.isEmpty())
            return;

        /*
         * Display extra properties in order in the report
         */
        final ValidationMessage.Builder msg = newMsg()
            .addInfo("unwanted", new TreeSet<String>(fields))
            .setMessage("additional properties not permitted");
        report.addMessage(msg.build());
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder(keyword + ": ");

        if (additionalOK)
            return sb.append("allowed").toString();

        sb.append("none");

        if (properties.isEmpty() && patternProperties.isEmpty())
            return sb.toString();

        sb.append(", unless: ");

        final Set<String> further = new LinkedHashSet<String>();

        if (!properties.isEmpty())
            further.add("one property is any of: " + properties);

        if (!patternProperties.isEmpty())
            further.add("a property matches any regex among: "
                + patternProperties);

        sb.append(TOSTRING_JOINER.join(further));

        return sb.toString();
    }
}
