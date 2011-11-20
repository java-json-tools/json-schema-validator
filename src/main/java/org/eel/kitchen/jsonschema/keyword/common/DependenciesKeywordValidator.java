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

package org.eel.kitchen.jsonschema.keyword.common;

import org.codehaus.jackson.JsonNode;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
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

    /**
     * Validate the {@code dependencies} keyword
     *
     * <p>The list of properties in {@code dependencies} is collected and
     * intersected with the list of properties in the instance. For each
     * remaining property:</p>
     * <ul>
     *     <li>if the dependency is one or more property name(s),
     *     then all these properties must be present in the instance;</li>
     *     <li>if it is a schema, then the current instance must be validated
     *     against this schema as well as the current one.</li>
     * </ul>
     *
     *
     * @param context the validation context
     * @param instance the instance to validte
     * @return the report
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonNode node = context.getSchema().get(keyword);
        final ValidationReport report = context.createReport();

        final SortedSet<String> fields = new TreeSet<String>();
        fields.addAll(CollectionUtils.toSet(instance.getFieldNames()));

        final SortedMap<String, JsonNode> dependencies
            = CollectionUtils.toSortedMap(node.getFields());

        dependencies.keySet().retainAll(fields);

        if (dependencies.isEmpty())
            return report;

        for (final Map.Entry<String, JsonNode> entry: dependencies.entrySet())
            report.mergeWith(doOneDependency(context, instance, entry));

        return report;
    }

    /**
     * Compute one dependency
     *
     * <p>This handles schema dependencies; property dependencies is handled to
     * {@link #doSimpleDependency(Map.Entry, Iterator, ValidationReport)}
     *
     * @param context the context
     * @param instance the instance
     * @param entry the dependency entry
     * @return the report
     * @throws JsonValidationFailureException on validation failure,
     * with the appropriate validation mode
     */
    private static ValidationReport doOneDependency(
        final ValidationContext context, final JsonNode instance,
        final Map.Entry<String, JsonNode> entry)
        throws JsonValidationFailureException
    {
        final JsonNode depnode = entry.getValue();

        if (!depnode.isObject()) {
            final ValidationReport depreport = context.createReport();
            doSimpleDependency(entry, instance.getFieldNames(), depreport);
            return depreport;
        }

        final ValidationContext ctx = context.withSchema(depnode);
        final Validator v = ctx.getValidator(instance);
        return v.validate(ctx, instance);
    }

    /**
     * Handle a property dependency
     *
     * @param entry the dependency entry
     * @param fieldNames the property names in the instance
     * @param depreport the report to fill
     * @throws JsonValidationFailureException on validation failure,
     * with the appropriate validation mode
     */
    private static void doSimpleDependency(
        final Map.Entry<String, JsonNode> entry,
        final Iterator<String> fieldNames, final ValidationReport depreport)
        throws JsonValidationFailureException
    {
        final String depname = entry.getKey();
        final JsonNode depnode = entry.getValue();

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

        depreport.fail("property " + depname + " is missing dependencies "
            + expected);
    }
}
