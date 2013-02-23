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

package com.github.fge.jsonschema.keyword.syntax.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.keyword.syntax.AbstractSyntaxChecker;
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
import static com.github.fge.jsonschema.util.NodeType.*;

/**
 * Helper class to validate the syntax of draft v3's {@code type} and {@code
 * disallow}
 */
public final class DraftV3TypeKeywordSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final String ANY = "any";
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    public DraftV3TypeKeywordSyntaxChecker(final String keyword)
    {
        super(keyword, STRING, ARRAY);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = tree.getNode().get(keyword);

        if (node.isTextual()) {
            if (!typeIsValid(node.textValue()))
                report.error(newMsg(tree, INCORRECT_PRIMITIVE_TYPE)
                    .put("valid", EnumSet.allOf(NodeType.class))
                    .put("found", node));
            return;
        }

        final int size = node.size();
        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        JsonNode element;
        NodeType type;
        boolean uniqueItems = true;

        for (int index = 0; index < size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            uniqueItems = set.add(EQUIVALENCE.wrap(element));
            if (type == OBJECT) {
                pointers.add(JsonPointer.of(keyword, index));
                continue;
            }
            if (type != STRING) {
                report.error(newMsg(tree, INCORRECT_ELEMENT_TYPE)
                    .put("index", index)
                    .put("expected", EnumSet.of(OBJECT, STRING))
                    .put("found", type));
                continue;
            }
            if (!typeIsValid(element.textValue()))
                report.error(newMsg(tree, INCORRECT_PRIMITIVE_TYPE)
                    .put("index", index)
                    .put("valid", EnumSet.allOf(NodeType.class))
                    .put("found", element));
        }

        if (!uniqueItems)
            report.error(newMsg(tree, ELEMENTS_NOT_UNIQUE));
    }

    private static boolean typeIsValid(final String s)
    {
        if (ANY.equals(s))
            return true;

        return NodeType.fromName(s) != null;
    }
}
