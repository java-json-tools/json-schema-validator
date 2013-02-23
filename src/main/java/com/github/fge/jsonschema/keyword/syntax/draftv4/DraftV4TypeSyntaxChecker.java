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
 * Syntax checker for draft v4's {@code type} keyword
 */
public final class DraftV4TypeSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final EnumSet<NodeType> ALL_TYPES
        = EnumSet.allOf(NodeType.class);

    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private static final SyntaxChecker INSTANCE
        = new DraftV4TypeSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private DraftV4TypeSyntaxChecker()
    {
        super("type", NodeType.ARRAY, NodeType.STRING);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);


        if (node.isTextual()) {
            final String s = node.textValue();
            if (NodeType.fromName(s) == null)
                report.error(newMsg(tree, INCORRECT_PRIMITIVE_TYPE)
                    .put("valid", ALL_TYPES).put("found", s));
            return;
        }

        final int size = node.size();

        if (size == 0) {
            report.error(newMsg(tree, EMPTY_ARRAY));
            return;
        }

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        JsonNode element;
        NodeType type;
        boolean uniqueElements = true;

        for (int index = 0; index <size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            uniqueElements = set.add(EQUIVALENCE.wrap(element));
            if (type != NodeType.STRING) {
                report.error(
                    newMsg(tree, INCORRECT_ELEMENT_TYPE).put("index", index)
                        .put("expected", NodeType.STRING).put("found", type));
                continue;
            }
            if (NodeType.fromName(element.textValue()) == null)
                report.error(newMsg(tree, INCORRECT_PRIMITIVE_TYPE)
                    .put("index", index).put("valid", ALL_TYPES)
                    .put("found", element));
        }

        if (!uniqueElements)
            report.error(newMsg(tree, ELEMENTS_NOT_UNIQUE));
    }
}
