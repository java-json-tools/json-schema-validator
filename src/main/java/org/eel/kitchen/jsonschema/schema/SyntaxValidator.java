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

package org.eel.kitchen.jsonschema.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.eel.kitchen.jsonschema.bundle.Keyword;
import org.eel.kitchen.jsonschema.bundle.KeywordBundle;
import org.eel.kitchen.jsonschema.bundle.KeywordBundles;
import org.eel.kitchen.jsonschema.main.ValidationReport;
import org.eel.kitchen.jsonschema.syntax.SyntaxChecker;
import org.eel.kitchen.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
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
// FIXME: try and recurse through schemas, it can be done with a little work,
// but it has to be triggered from _within_ validators. Ouch.
public final class SyntaxValidator
{
    //FIXME: make this a "LRUSet"
    private final Set<JsonNode> done = new HashSet<JsonNode>();

    private final Map<String, SyntaxChecker> checkers
        = new HashMap<String, SyntaxChecker>();

    public SyntaxValidator(final KeywordBundle bundle)
    {
        String name;
        SyntaxChecker checker;

        for (final Map.Entry<String, Keyword> entry: bundle) {
            name = entry.getKey();
            checker = entry.getValue().getSyntaxChecker();
            if (checker != null)
                checkers.put(name, checker);
        }
    }

    public SyntaxValidator()
    {
        this(KeywordBundles.defaultBundle());
    }

    public synchronized void validate(final ValidationReport report,
        final JsonNode schema)
    {
        if (done.contains(schema))
            return;

        final Set<String> keywords = CollectionUtils.toSet(schema.fieldNames());

        keywords.retainAll(checkers.keySet());

        for (final String keyword : keywords)
            checkers.get(keyword).checkSyntax(report, schema);

        if (report.isSuccess())
            done.add(schema);
    }
}
