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

package org.eel.kitchen.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.report.ValidationDomain;
import org.eel.kitchen.jsonschema.report.ValidationMessage;
import org.eel.kitchen.jsonschema.util.JacksonUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Schema syntax validator
 *
 * <p>Note that schemas are not validated "in depth", only the first level of
 * keywords will be validated. This is by design (for now) since recursing
 * through schemas is not as simple as it sounds, we cannot just blindly
 * recurse through object instances (consider {@code enum}).</p>
 */
public final class SyntaxValidator
{
    /**
     * List of registered checkers
     */
    private final Map<String, SyntaxChecker> checkers;

    /**
     * Constructor
     *
     * @param bundle the keyword bundle
     */
    public SyntaxValidator(final KeywordBundle bundle)
    {
        checkers = ImmutableMap.copyOf(bundle.getSyntaxCheckers());
    }

    /**
     * Validate one schema
     *
     * @param messages the list of messages to fill in the event of a failure
     * @param schema the schema to analyze
     */
    public void validate(final List<ValidationMessage> messages,
        final JsonNode schema)
    {
        final Set<String> keywords = JacksonUtils.fieldNames(schema);

        keywords.retainAll(checkers.keySet());

        ValidationMessage.Builder msg;
        SyntaxChecker checker;

        for (final String keyword: keywords) {
            msg = new ValidationMessage.Builder(ValidationDomain.SYNTAX)
                .setKeyword(keyword);
            checker = checkers.get(keyword);
            checker.checkSyntax(msg, messages, schema);
        }
    }
}
