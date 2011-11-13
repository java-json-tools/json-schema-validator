/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.ValidationReport;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.context.ValidationContext;
import org.eel.kitchen.util.CollectionUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Keyword validation for the {@code dependencies} keyword (draft section 5
 * .8). It supports both simple dependencies (ie, other property names) or
 * schema dependencies.
 */
public final class DependenciesKeywordValidator
    extends KeywordValidator
{
    public DependenciesKeywordValidator()
    {
        super("dependencies");
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchemaNode();

        final SortedSet<String> fields = new TreeSet<String>();
        fields.addAll(CollectionUtils.toSet(instance.getFieldNames()));

        final JsonNode node = schema.get(keyword);
        final SortedMap<String, JsonNode> dependencies
            = CollectionUtils.toSortedMap(node.getFields());

        dependencies.keySet().retainAll(fields);

        if (dependencies.isEmpty())
            return report;

        for (final Map.Entry<String, JsonNode> entry: dependencies.entrySet()) {
            report.mergeWith(doOneDependency(context, instance, entry));
        }

        return report;
    }

    private static ValidationReport doOneDependency(
        final ValidationContext context, final JsonNode instance,
        final Map.Entry<String, JsonNode> entry)
    {
        final JsonNode depnode = entry.getValue();

        if (!depnode.isObject()) {
            final ValidationReport depreport = context.createReport();
            final String depname = entry.getKey();
            doSimpleDependency(depname, depnode, instance.getFieldNames(),
                depreport);
            return depreport;
        }

        final ValidationContext ctx = context.createContext(depnode);
        final Validator v = ctx.getValidator(instance);
        return v.validate(ctx, instance);
    }

    private static void doSimpleDependency(final String depname,
        final JsonNode depnode, final Iterator<String> fieldNames,
        final ValidationReport depreport)
    {
        final SortedSet<String> expected = new TreeSet<String>();

        if (depnode.isTextual())
            expected.add(depnode.getTextValue());
        else
            for (final JsonNode element: depnode)
                expected.add(element.getTextValue());

        while (fieldNames.hasNext())
            expected.remove(fieldNames.next());

        if (expected.isEmpty())
            return;

        depreport.addMessage("property " + depname + " is missing dependencies "
            + expected);
    }
}
