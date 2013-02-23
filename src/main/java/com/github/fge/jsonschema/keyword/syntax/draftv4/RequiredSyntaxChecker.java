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

package com.github.fge.jsonschema.keyword.syntax.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.syntax.AbstractSyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

/**
 * Syntax checker for draft v4's {@code required} keyword
 */
public final class RequiredSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private static final SyntaxChecker INSTANCE = new RequiredSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private RequiredSyntaxChecker()
    {
        super("required", NodeType.ARRAY);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);
        final int size = node.size();

        if (size == 0) {
            report.error(newMsg(tree, EMPTY_ARRAY));
            return;
        }

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        boolean uniqueElements = true;
        JsonNode element;
        NodeType type;

        for (int index = 0; index < size; index++) {
            element = node.get(index);
            uniqueElements = set.add(EQUIVALENCE.wrap(element));
            type = NodeType.getNodeType(element);
            if (type != NodeType.STRING)
                report.error(newMsg(tree, INCORRECT_ELEMENT_TYPE)
                    .put("index", index)
                    .put("expected", EnumSet.of(NodeType.STRING))
                    .put("found", type)
                );
        }

        if (!uniqueElements)
            report.error(newMsg(tree, ELEMENTS_NOT_UNIQUE));
    }
}
