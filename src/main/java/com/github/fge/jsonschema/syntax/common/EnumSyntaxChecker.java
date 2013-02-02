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

package com.github.fge.jsonschema.syntax.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.processing.ProcessingException;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.syntax.SyntaxChecker;
import com.github.fge.jsonschema.tree.JsonSchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

public final class EnumSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private static final SyntaxChecker INSTANCE = new EnumSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private EnumSyntaxChecker()
    {
        super("enum", NodeType.ARRAY);
    }
    @Override
    protected void checkValue(Collection<JsonPointer> pointers,
        ProcessingReport report, JsonSchemaTree tree)
        throws ProcessingException
    {
        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        for (final JsonNode element: getNode(tree))
            if (!set.add(EQUIVALENCE.wrap(element))) {
                report.error(newMsg(tree, ITEMS_NOT_UNIQUE));
                return;
            }
    }
}
