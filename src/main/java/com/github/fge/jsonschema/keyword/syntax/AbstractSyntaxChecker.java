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

package com.github.fge.jsonschema.keyword.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.messages.SyntaxMessages;
import com.github.fge.jsonschema.processors.ValidationDomain;
import com.github.fge.jsonschema.ref.JsonPointer;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.NodeType;

import java.util.Collection;
import java.util.EnumSet;

public abstract class AbstractSyntaxChecker
    implements SyntaxChecker
{
    protected final String keyword;
    private final EnumSet<NodeType> types;

    protected AbstractSyntaxChecker(final String keyword, final NodeType first,
        final NodeType... other)
    {
        this.keyword = keyword;
        types = EnumSet.of(first, other);
    }

    @Override
    public final EnumSet<NodeType> getValidTypes()
    {
        return EnumSet.copyOf(types);
    }

    @Override
    public final void checkSyntax(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);
        final NodeType type = NodeType.getNodeType(node);

        if (!types.contains(type)) {
            report.error(newMsg(tree, SyntaxMessages.INCORRECT_TYPE)
                .put("expected", types).put("found", type));
            return;
        }

        checkValue(pointers, report, tree);
    }

    protected abstract void checkValue(final Collection<JsonPointer> pointers,
        final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException;

    protected final <T> ProcessingMessage newMsg(final SchemaTree tree,
        final T msg)
    {
        return new ProcessingMessage().put("domain", "syntax")
            .put("schema", tree).put("keyword", keyword).message(msg)
            .setExceptionProvider(ValidationDomain.SYNTAX.exceptionProvider());
    }

    protected final JsonNode getNode(final SchemaTree tree)
    {
        return tree.getNode().get(keyword);
    }
}
