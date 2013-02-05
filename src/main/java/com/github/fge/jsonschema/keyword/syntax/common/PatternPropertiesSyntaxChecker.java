/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.jsonschema.keyword.syntax.common;

import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.SchemaMapSyntaxChecker;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.RhinoHelper;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

public final class PatternPropertiesSyntaxChecker
    extends SchemaMapSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new PatternPropertiesSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private PatternPropertiesSyntaxChecker()
    {
        super("patternProperties");
    }

    @Override
    protected void extraChecks(final ProcessingReport report,
        final JsonSchemaTree tree)
        throws ProcessingException
    {
        /*
         * Check that the member names are regexes
         */
        final Set<String> set = Sets.newHashSet(getNode(tree).fieldNames());

        for (final String s: Ordering.natural().sortedCopy(set))
            if (!RhinoHelper.regexIsValid(s))
                report.error(newMsg(tree, INVALID_REGEX_MEMBER_NAME)
                    .put("memberName", s));
    }
}
