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
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.keyword.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.JsonValidationFailureException;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Keyword validator for the {@code required} keyword (draft v4)
 */
public final class RequiredKeywordValidator
    extends KeywordValidator
{
    private static final RequiredKeywordValidator instance
        = new RequiredKeywordValidator();

    private RequiredKeywordValidator()
    {
        super("required");
    }

    public static RequiredKeywordValidator getInstance()
    {
        return instance;
    }

    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
        throws JsonValidationFailureException
    {
        final JsonNode node = context.getSchema().get("required");
        final ValidationReport report = context.createReport();

        final SortedSet<String> required = new TreeSet<String>();

        for (final JsonNode element: node)
            required.add(element.textValue());

        final Set<String> instanceFields
            = CollectionUtils.toSet(instance.fieldNames());

        required.removeAll(instanceFields);

        if (!required.isEmpty())
            report.message("required properties " + required + " are missing");

        return report;
    }
}
