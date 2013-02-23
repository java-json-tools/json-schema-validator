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

package com.github.fge.jsonschema.keyword.syntax.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.syntax.SyntaxChecker;
import com.github.fge.jsonschema.keyword.syntax.helpers.DependenciesSyntaxChecker;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Set;

import static com.github.fge.jsonschema.messages.SyntaxMessages.*;

/**
 * Syntax checker for draft v3's {@code dependencies} keyword
 */
public final class DraftV3DependenciesSyntaxChecker
    extends DependenciesSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new DraftV3DependenciesSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private DraftV3DependenciesSyntaxChecker()
    {
        super(NodeType.ARRAY, NodeType.STRING);
    }

    @Override
    protected void checkDependency(final ProcessingReport report,
        final String name, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree).get(name);
        NodeType type;

        type = NodeType.getNodeType(node);

        if (type == NodeType.STRING)
            return;

        if (type != NodeType.ARRAY) {
            report.error(newMsg(tree, INCORRECT_DEPENDENCY_VALUE)
                .put("property", name).put("expected", dependencyTypes)
                .put("found", type));
            return;
        }

        final int size = node.size();

        /*
         * Yep, in draft v3, nothing prevents a dependency array from being
         * empty! This is stupid, so at least warn the user.
         */
        if (size == 0) {
            report.warn(newMsg(tree, EMPTY_ARRAY).put("property", name));
            return;
        }

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        JsonNode element;
        boolean uniqueElements = true;

        for (int index = 0; index < size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            uniqueElements = set.add(EQUIVALENCE.wrap(element));
            if (type == NodeType.STRING)
                continue;
            report.error(newMsg(tree, INCORRECT_ELEMENT_TYPE)
                .put("property", name).put("index", index)
                .put("expected", EnumSet.of(NodeType.STRING))
                .put("found", type));
        }

        /*
         * Similarly, there is nothing preventing duplicates. Equally stupid,
         * so warn the user.
         */
        if (!uniqueElements)
            report.warn(newMsg(tree, ELEMENTS_NOT_UNIQUE)
                .put("property", name));
    }
}
