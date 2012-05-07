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

package org.eel.kitchen.jsonschema.keyword.common;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.keyword.KeywordValidator;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.util.CollectionUtils;
import org.eel.kitchen.util.RhinoHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Validator for the {@code additionalProperties} keyword (draft section 5.4)
 */
public final class AdditionalPropertiesKeywordValidator
    extends KeywordValidator
{
    private static final AdditionalPropertiesKeywordValidator instance
        = new AdditionalPropertiesKeywordValidator();

    private AdditionalPropertiesKeywordValidator()
    {
        super("additionalProperties");
    }

    public static AdditionalPropertiesKeywordValidator getInstance()
    {
        return instance;
    }

    /**
     * Validate {@code additionalProperties}
     *
     * <p>The rules for {@code additionalProperties} are as follows:</p>
     * <ul>
     *     <li>if it is anything else than {@code false},
     *     the validation succeeds;</li>
     *     <li>otherwise, the validation fails if at least one property of
     *     the instance:
     *     </li>
     *     <ul>
     *         <li>is not found in {@code properties}, and</li>
     *         <li>does not match any regex in {@code patternProperties}.</li>
     *     </ul>
     * </ul>
     *
     *
     *
     * @param context the validation context
     * @param instance the instance to validate
     * @return the report
     */
    @Override
    public ValidationReport validate(final ValidationContext context,
        final JsonNode instance)
    {
        final ValidationReport report = context.createReport();
        final JsonNode schema = context.getSchema();

        final boolean shortcut = schema.get(keyword).asBoolean(true);

        if (shortcut)
            return report;

        final Set<String> properties = new HashSet<String>();
        final Set<String> patterns = new HashSet<String>();

        JsonNode node;

        if (schema.has("properties")) {
            node = schema.get("properties");
            properties.addAll(CollectionUtils.toSet(node.fieldNames()));
        }

        if (schema.has("patternProperties")) {
            node = schema.get("patternProperties");
            patterns.addAll(CollectionUtils.toSet(node.fieldNames()));
        }

        final Set<String> fields =
            CollectionUtils.toSet(instance.fieldNames());

        fields.removeAll(properties);

        if (fields.isEmpty())
            return report;

        if (patterns.isEmpty()) {
            report.message("additional properties are not permitted");
            return report;
        }

        for (final String field: fields)
            if (!patternsMatch(patterns, field)) {
                report.message("additional properties are not permitted");
                break;
            }

        return report;
    }

    /**
     * See if a property name matches one regex found in {@code
     * patternProperties} (if any)
     *
     * @param patterns the list of patterns
     * @param field the property name
     * @return true on a match
     */
    private static boolean patternsMatch(final Set<String> patterns,
        final String field)
    {
        for (final String regex: patterns)
            if (RhinoHelper.regMatch(regex, field))
                return true;

        return false;
    }
}
