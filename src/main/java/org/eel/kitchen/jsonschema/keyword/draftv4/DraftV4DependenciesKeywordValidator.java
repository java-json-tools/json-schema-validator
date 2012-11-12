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

package org.eel.kitchen.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.report.Message;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JacksonUtils;
import org.eel.kitchen.jsonschema.util.NodeType;
import org.eel.kitchen.jsonschema.validator.JsonValidator;
import org.eel.kitchen.jsonschema.validator.ValidationContext;

import java.util.Map;
import java.util.Set;

public final class DraftV4DependenciesKeywordValidator
    extends KeywordValidator
{
    private final Map<String, JsonNode> dependencies;

    public DraftV4DependenciesKeywordValidator(final JsonNode schema)
    {
        super("dependencies", NodeType.OBJECT);
        dependencies = ImmutableMap.copyOf(
            JacksonUtils.nodeToMap(schema.get(keyword))
        );
    }

    @Override
    protected void validate(final ValidationContext context,
        final ValidationReport report, final JsonNode instance)
    {
        final Set<String> fields = Sets.newHashSet(instance.fieldNames());
        final Set<String> unsatisfied = Sets.newTreeSet();

        fields.retainAll(dependencies.keySet());

        JsonNode subSchema;
        JsonValidator validator;
        ValidationReport subReport;

        for (final String field: fields) {
            subSchema = dependencies.get(field);
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

    @Override
    public String toString()
    {
        return "schema dependencies on properties: " + dependencies.keySet();
    }
}
