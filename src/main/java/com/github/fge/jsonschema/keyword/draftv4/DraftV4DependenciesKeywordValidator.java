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

package com.github.fge.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.keyword.KeywordValidator;
import com.github.fge.jsonschema.report.Message;
import com.github.fge.jsonschema.report.ValidationReport;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.jackson.JacksonUtils;
import com.github.fge.jsonschema.validator.JsonValidator;
import com.github.fge.jsonschema.validator.ValidationContext;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * Keyword validator for the (draft v4) {@code dependencies} keyword
 *
 * <p>Unlike in draft v3, property dependencies can now only be arrays of
 * strings.</p>
 */
public final class DraftV4DependenciesKeywordValidator
    extends KeywordValidator
{
    private final Map<String, JsonNode> schemaDeps = Maps.newHashMap();
    private final Map<String, Set<String>> propertyDeps = Maps.newHashMap();

    public DraftV4DependenciesKeywordValidator(final JsonNode schema)
    {
        super("dependencies", NodeType.OBJECT);

        final Map<String, JsonNode> map
            = JacksonUtils.asMap(schema.get(keyword));

        String key;
        JsonNode value;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            if (value.isObject()) { // Schema dependency
                schemaDeps.put(key, value);
                continue;
            }
            final Set<String> set = Sets.newHashSet();
            for (final JsonNode node: value)
                set.add(node.textValue());
            propertyDeps.put(key, set);
        }
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        if (!schemaDeps.isEmpty())
            processSchemaDeps(context, report, instance);
        if (!propertyDeps.isEmpty())
            processPropertyDeps(report, instance);
    }

    private void processSchemaDeps(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final Set<String> fields = Sets.newHashSet(instance.fieldNames());
        final Set<String> unsatisfied = Sets.newTreeSet();

        fields.retainAll(schemaDeps.keySet());

        JsonNode subSchema;
        JsonValidator validator;
        ValidationReport subReport;

        for (final String field: fields) {
            subSchema = schemaDeps.get(field);
            validator = context.newValidator(subSchema);
            subReport = report.copy();
            validator.validate(context, subReport, instance);
            if (subReport.isSuccess())
                continue;
            unsatisfied.add(field);
            report.mergeWith(subReport);
        }

        if (unsatisfied.isEmpty())
            return;

        final Message.Builder msg = newMsg().addInfo("unsatisfied", unsatisfied)
            .setMessage("unsatisfied schema dependencies");
        report.addMessage(msg.build());
    }

    private void processPropertyDeps(final ValidationReport report,
        final JsonNode instance)
    {
        final Set<String> fields = Sets.newHashSet(instance.fieldNames());

        Set<String> deps, missing;

        for (final String field: fields) {
            deps = propertyDeps.get(field);
            if (deps == null)
                continue;
            missing = Sets.newHashSet(deps);
            missing.removeAll(fields);
            if (missing.isEmpty())
                continue;
            report.addMessage(newMsg().addInfo("property", field)
                .setMessage("unsatisfied property dependencies")
                .addInfo("required", Ordering.natural().sortedCopy(deps))
                .addInfo("missing", Ordering.natural().sortedCopy(missing))
                .build());
        }
    }

    @Override
    public String toString()
    {
        return "schema dependencies on properties: " + schemaDeps.keySet()
            + "; property dependencies on properties: " + propertyDeps.keySet();
    }
}
